package org.fcnabc.autoppt.slides.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.slides.v1.Slides;
import com.google.api.services.slides.v1.model.BatchUpdatePresentationRequest;
import com.google.api.services.slides.v1.model.DuplicateObjectRequest;
import com.google.api.services.slides.v1.model.Page;
import com.google.api.services.slides.v1.model.Presentation;
import com.google.api.services.slides.v1.model.Request;
import com.google.api.services.slides.v1.model.PageElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GoogleSlides {
    private List<Request> requests = new ArrayList<>();
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

    // -------------------------- EXECUTE METHODS ----------------------------

    public void executeBatchUpdate(String presentationId) throws IOException {
        if (requests.isEmpty()) return;

        BatchUpdatePresentationRequest body = new BatchUpdatePresentationRequest().setRequests(requests);
        requests.clear();
        service.presentations().batchUpdate(presentationId, body).execute();
    }

    // ------------------------- DUPLICATION METHODS -------------------------

    /**
     * Duplicate an entire presentation on execution (synchronous)
     */
    public String duplicatePresentation(String presentationId, String newTitle) throws IOException {
        Presentation newPresentation = new Presentation().setTitle(newTitle);
        Presentation createdPresentation = service.presentations().create(newPresentation).execute();
        return createdPresentation.getPresentationId();
    }

    /**
     * Duplicate a slide within the presentation; the duplicated slide is placed right after the source slide. 
     * Returns a mapping from original object IDs to duplicated object IDs for the duplicated slide and all text elements within it, 
     * which can be used for subsequent updates to the duplicated content within the same batch request.
     */
    public Map<String, String> duplicateSlide(Presentation presentation, int slideIndex) {
        String slideId = presentation.getSlides().get(slideIndex).getObjectId();

        Map<String, String> dupObjectMapping = new HashMap<>();
        dupObjectMapping.put(slideId, slideId + (duplicateCounter++));
        getAllTextElementsInSlide(presentation, slideIndex).entrySet().stream()
            .forEach(entry -> dupObjectMapping.put(entry.getKey(), entry.getKey() + (duplicateCounter++)));

        DuplicateObjectRequest duplicateRequest = new DuplicateObjectRequest()
                .setObjectId(slideId)
                .setObjectIds(dupObjectMapping);
        Request request = new Request().setDuplicateObject(duplicateRequest);
        requests.add(request);
        return dupObjectMapping;
    }

    // ---------------------------- GETTER METHODS ---------------------------

    public Presentation getPresentation(String presentationId) throws IOException {
        return service.presentations().get(presentationId).execute();
    }

    public int getSlideCount(Presentation presentation) {
        return presentation.getSlides().size();
    }

    public Map<String, String> getAllTextElementsInSlide(Presentation presentation, int slideIndex) {
        Page slide = presentation.getSlides().get(slideIndex);
        Map<String, String> textElements = new HashMap<>();
        for (var element : slide.getPageElements()) {
            if (element.getShape() != null && element.getShape().getText() != null) {
                String elementId = element.getObjectId();
                String textContent = element.getShape().getText().getTextElements().stream()
                        .filter(te -> te.getTextRun() != null)
                        .map(te -> te.getTextRun().getContent())
                        .reduce("", String::concat);
                if (!textContent.isBlank()) {
                    textElements.put(elementId, textContent);
                }
            }
        }
        return textElements;
    }

    public List<String> getTablesInSlide(Presentation presentation, int slideIndex) {
        Page slide = presentation.getSlides().get(slideIndex);
        List<String> tableIds = new ArrayList<>();
        for (PageElement element : slide.getPageElements()) {
            if (element.getTable() != null) {
                tableIds.add(element.getObjectId());
            }
        }
        return tableIds;
    }

    // ---------------------------- SETTER METHODS ---------------------------
}