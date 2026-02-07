package org.fcnabc.autoppt.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.DeleteTextRequest;
import com.google.api.services.slides.v1.model.Dimension;
import com.google.api.services.slides.v1.model.DuplicateObjectRequest;
import com.google.api.services.slides.v1.model.InsertTextRequest;
import com.google.api.services.slides.v1.model.OpaqueColor;
import com.google.api.services.slides.v1.model.OptionalColor;
import com.google.api.services.slides.v1.model.Page;
import com.google.api.services.slides.v1.model.PageElement;
import com.google.api.services.slides.v1.model.ParagraphStyle;
import com.google.api.services.slides.v1.model.Presentation;
import com.google.api.services.slides.v1.model.Range;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.RgbColor;
import com.google.api.services.slides.v1.model.Table;
import com.google.api.services.slides.v1.model.TableCell;
import com.google.api.services.slides.v1.model.TableCellLocation;
import com.google.api.services.slides.v1.model.TableRow;
import com.google.api.services.slides.v1.model.TextStyle;
import com.google.api.services.slides.v1.model.UpdateParagraphStyleRequest;
import com.google.api.services.slides.v1.model.UpdateTextStyleRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.google.models.ParagraphAlignment;
import org.fcnabc.autoppt.google.models.SlideObject;

/**
 * A wrapper around the Google Slides API that provides higher-level methods for common operations on slides, 
 * such as duplication, deletion, and text updates.
 * To optimize performance, this class batches multiple operations together and executes them in a single API call.
 */
public class GoogleSlides {
    private static final String DUPLICATE_SUFFIX = "_d%d";
    private static final String FONT_DIMENSION_UNIT = "PT";
    private static final String PRESENTATION_CONTEXT = "presentationId,slides";

    private List<Request> requests = new ArrayList<>();
    private Presentation presentation = null;
    private int duplicateCounter = 0;

    private final Credential credential;
    private Slides service;

    @Inject
    public GoogleSlides(Credential credential, @Named("googleAppName") String applicationName) {
        this.credential = credential;
        this.service = new Slides.Builder(
                this.credential.getTransport(),
                this.credential.getJsonFactory(),
                this.credential)
                .setApplicationName(applicationName)
                .build();
    }

    // ------------------------- VALIDATION METHODS ----------------------------

    private void validatePresentationContext() {
        if (this.presentation == null) {
            throw new IllegalStateException("Presentation context is not set. Call setPresentationContext() first.");
        }
    }

    private void validateSlideIndex(int slideIndex) {
        validatePresentationContext();
        if (slideIndex < 0 || slideIndex >= getSlideCount()) {
            throw new IndexOutOfBoundsException("Invalid slide index: " + slideIndex);
        }
    }

    // -------------------------- EXECUTE METHODS ----------------------------

    /**
     * Sets the current presentation context by loading the presentation with the given ID. This also resets any pending requests.
     * Run this before performing any operations on slides!
     * Retrieve only the necessary fields (presentationId and slides) to minimize API response size and improve performance.
     */
    public void setPresentationContext(String presentationId) throws IOException {
        this.requests.clear();
        this.presentation = service.presentations().get(presentationId)
                    .setFields(PRESENTATION_CONTEXT)
                    .execute();
    }

    public void executeUpdates() throws IOException {
        validatePresentationContext();

        if (requests.isEmpty()) return;

        BatchUpdatePresentationRequest body = new BatchUpdatePresentationRequest().setRequests(requests);
        String presentationId = this.presentation.getPresentationId();

        // Execute the batch update and refresh the presentation state
        service.presentations().batchUpdate(presentationId, body).execute();
        setPresentationContext(presentationId);
    }

    // ------------------------- DUPLICATION METHODS -------------------------

    /**
     * Duplicate a slide within the presentation; the duplicated slide is placed right after the source slide. 
     * Returns a mapping from original object IDs to duplicated object IDs for the duplicated slide and all text elements within it, 
     * which can be used for subsequent updates to the duplicated content within the same batch request.
     */
    public Map<SlideObject, SlideObject> duplicateSlide(int slideIndex) {
        validatePresentationContext();
        validateSlideIndex(slideIndex);

        String slideId = getSlideId(slideIndex);

        Map<SlideObject, SlideObject> dupObjectMapping = new HashMap<>();
        dupObjectMapping.put(new SlideObject(slideId), new SlideObject(slideId + String.format(DUPLICATE_SUFFIX, duplicateCounter++)));
        getTextElementsInSlide(slideIndex).keySet().forEach(so -> 
             dupObjectMapping.put(so,
                new SlideObject(so.id() + String.format(DUPLICATE_SUFFIX, duplicateCounter++), so.rowIndex(), so.columnIndex(), so.isTableCell()))
        );

        Map<String, String> dupIdMapping = new HashMap<>();
        dupObjectMapping.forEach((k, v) -> dupIdMapping.put(k.id(), v.id()));

        DuplicateObjectRequest duplicateRequest = new DuplicateObjectRequest()
                .setObjectId(slideId)
                .setObjectIds(dupIdMapping);
        Request request = new Request().setDuplicateObject(duplicateRequest);
        requests.add(request);
        return dupObjectMapping;
    }

    // --------------------------- DELETION METHODS --------------------------

    public void deleteSlide(String slideId) {
        Request deleteRequest = new Request().setDeleteObject(
            new com.google.api.services.slides.v1.model.DeleteObjectRequest().setObjectId(slideId));
        requests.add(deleteRequest);
    }

    // ---------------------------- GETTER METHODS ---------------------------

    public String getSlideId(int slideIndex) {
        validatePresentationContext();
        validateSlideIndex(slideIndex);
        return presentation.getSlides().get(slideIndex).getObjectId();
    }

    public int getSlideCount() {
        validatePresentationContext();
        return presentation.getSlides().size();
    }

    /**
     * Extracts text content from textbox and table elements in the specified slide 
     * and returns a mapping from element IDs to their text content.
     * 
     * PageElement can only assume one type at a time
     * https://developers.google.com/workspace/slides/api/concepts/page-elements#page_elements
     */
    public Map<SlideObject, String> getTextElementsInSlide(int slideIndex) {
        validatePresentationContext();
        Page slide = presentation.getSlides().get(slideIndex);
        Map<SlideObject, String> textElements = new HashMap<>();
        if (slide.getPageElements() == null) return textElements;

        for (PageElement element : slide.getPageElements()) {
            String objectId = element.getObjectId();

            if (element.getShape() != null && element.getShape().getText() != null) {
                String textContent = element.getShape().getText().getTextElements().stream()
                        .filter(te -> te.getTextRun() != null)
                        .map(te -> te.getTextRun().getContent())
                        .reduce("", String::concat);
                textElements.put(new SlideObject(objectId), textContent);
            } else if (element.getTable() != null) {
                Table table = element.getTable();
                for (int r = 0; r < table.getTableRows().size(); r++) {
                    TableRow row = table.getTableRows().get(r);
                    for (int c = 0; c < row.getTableCells().size(); c++) {
                        TableCell cell = row.getTableCells().get(c);
                        if (cell.getText() != null) {
                            String textContent = cell.getText().getTextElements().stream()
                                    .filter(te -> te.getTextRun() != null)
                                    .map(te -> te.getTextRun().getContent())
                                    .reduce("", String::concat);
                            textElements.put(new SlideObject(objectId, r, c), textContent);
                        }
                    }
                }
            }
        }
        return textElements;
    }

    public List<SlideObject> getTablesInSlide(int slideIndex) {
        validatePresentationContext();
        Page slide = presentation.getSlides().get(slideIndex);
        List<SlideObject> tableObjects = new ArrayList<>();
        if (slide.getPageElements() == null) return tableObjects;
        
        for (PageElement element : slide.getPageElements()) {
            if (element.getTable() != null) {
                tableObjects.add(new SlideObject(element.getObjectId()));
            }
        }
        return tableObjects;
    }

    // ---------------------------- SETTER METHODS ---------------------------

    public void setText(SlideObject slideObject, String newText) {
        newText = Objects.requireNonNullElse(newText, "");

        DeleteTextRequest deleteRequest = new DeleteTextRequest()
                .setObjectId(slideObject.id())
                .setTextRange(new Range().setType("ALL"));
        InsertTextRequest insertRequest = new InsertTextRequest()
                .setObjectId(slideObject.id())
                .setText(newText)
                .setInsertionIndex(0);

        if (slideObject.isTableCell()) {
            TableCellLocation cellLocation = new TableCellLocation()
                    .setRowIndex(slideObject.rowIndex())
                    .setColumnIndex(slideObject.columnIndex());
            deleteRequest.setCellLocation(cellLocation);
            insertRequest.setCellLocation(cellLocation);
        }
        
        requests.add(new Request().setDeleteText(deleteRequest));
        requests.add(new Request().setInsertText(insertRequest));
    }

    public void setTextColor(SlideObject slideObject, String colorHex) {
        float r = Integer.parseInt(colorHex.substring(1, 3), 16) / 255.0f;
        float g = Integer.parseInt(colorHex.substring(3, 5), 16) / 255.0f;
        float b = Integer.parseInt(colorHex.substring(5, 7), 16) / 255.0f;

        UpdateTextStyleRequest textStyleRequest = new UpdateTextStyleRequest()
                .setObjectId(slideObject.id())
                .setStyle(new TextStyle()
                        .setForegroundColor(new OptionalColor()
                                .setOpaqueColor(new OpaqueColor()
                                        .setRgbColor(new RgbColor()
                                                .setRed(r)
                                                .setGreen(g)
                                                .setBlue(b)))))
                .setTextRange(new Range().setType("ALL"))
                .setFields("foregroundColor");

        if (slideObject.isTableCell()) {
            TableCellLocation cellLocation = new TableCellLocation()
                    .setRowIndex(slideObject.rowIndex())
                    .setColumnIndex(slideObject.columnIndex());
            textStyleRequest.setCellLocation(cellLocation);
        }

        requests.add(new Request().setUpdateTextStyle(textStyleRequest));
    }

    public void setStyle(
        SlideObject slideObject, 
        int startIndex, 
        int endIndex, 
        boolean isBold, 
        boolean isItalic,
        boolean isUnderline,
        boolean isSuperScript,
        double fontSize,
        String fontFamily
    ) {
        TextStyle textStyle = new TextStyle();
        textStyle.setBold(isBold);
        textStyle.setItalic(isItalic);
        textStyle.setUnderline(isUnderline);

        StringBuilder sb = new StringBuilder();
        sb.append("bold,italic,underline");

        if (isSuperScript) {
            textStyle.setBaselineOffset("SUPERSCRIPT");
            sb.append(",baselineOffset");
        }
        if (fontSize > 0) {
            textStyle.setFontSize(new Dimension().setMagnitude(fontSize).setUnit(FONT_DIMENSION_UNIT));
            sb.append(",fontSize");
        }
        if (fontFamily != null && !fontFamily.isEmpty()) {
            textStyle.setFontFamily(fontFamily);
            sb.append(",fontFamily");
        }

        UpdateTextStyleRequest textStyleRequest = new UpdateTextStyleRequest()
                .setObjectId(slideObject.id())
                .setStyle(textStyle)
                .setTextRange(new Range()
                        .setType("FIXED_RANGE")
                        .setStartIndex(startIndex)
                        .setEndIndex(endIndex))
                .setFields(sb.toString());

        if (slideObject.isTableCell()) {
            TableCellLocation cellLocation = new TableCellLocation()
                    .setRowIndex(slideObject.rowIndex())
                    .setColumnIndex(slideObject.columnIndex());
            textStyleRequest.setCellLocation(cellLocation);
        }

        requests.add(new Request().setUpdateTextStyle(textStyleRequest));
    }

    public void setParagraphStyle(SlideObject slideObject, float lineSpacing, ParagraphAlignment alignment) {
        ParagraphStyle paragraphStyle = new ParagraphStyle();
        paragraphStyle.setLineSpacing(lineSpacing);
        paragraphStyle.setAlignment(alignment.getDisplayString());

        UpdateParagraphStyleRequest paragraphStyleRequest = new UpdateParagraphStyleRequest()
                .setObjectId(slideObject.id())
                .setStyle(paragraphStyle)
                .setTextRange(new Range().setType("ALL"))
                .setFields("lineSpacing,alignment");

        if (slideObject.isTableCell()) {
            TableCellLocation cellLocation = new TableCellLocation()
                    .setRowIndex(slideObject.rowIndex())
                    .setColumnIndex(slideObject.columnIndex());
            paragraphStyleRequest.setCellLocation(cellLocation);
        }

        requests.add(new Request().setUpdateParagraphStyle(paragraphStyleRequest));
    }

    public void setSpaceAbove(SlideObject slideObject, int paragraphIndex, double spaceAbove) {
        ParagraphStyle paragraphStyle = new ParagraphStyle();
        paragraphStyle.setSpaceAbove(new Dimension().setMagnitude(spaceAbove).setUnit(FONT_DIMENSION_UNIT));

        UpdateParagraphStyleRequest paragraphStyleRequest = new UpdateParagraphStyleRequest()
                .setObjectId(slideObject.id())
                .setStyle(paragraphStyle)
                .setTextRange(new Range().setType("FIXED_RANGE").setStartIndex(paragraphIndex).setEndIndex(paragraphIndex + 1))
                .setFields("spaceAbove");

        if (slideObject.isTableCell()) {
            TableCellLocation cellLocation = new TableCellLocation()
                    .setRowIndex(slideObject.rowIndex())
                    .setColumnIndex(slideObject.columnIndex());
            paragraphStyleRequest.setCellLocation(cellLocation);
        }

        requests.add(new Request().setUpdateParagraphStyle(paragraphStyleRequest));
    }
}