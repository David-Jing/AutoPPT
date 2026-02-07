package org.fcnabc.autoppt.google.models;

/**
 * Represents a single object on a slide, which can be a table cell within a slide.
 * A table cell is a single cell within a table object, located via x (column) and y (row) index.
 */
public record SlideObject(
    String id, // Object ID or Table Cell ID
    int rowIndex,
    int columnIndex,
    boolean isTableCell
) {
    /**
     * Constructor for non-table cell objects. The isTableCell flag is set to false, and rowIndex and columnIndex are set to -1.
     * A table object is not a table cell; it represents the entire table.
     */
    public SlideObject(String id) {
        this(id, -1, -1, false);
    }

    /**
     * Constructor for table cell objects. A table cell is a single cell within a table object,
     * located via x (column) and y (row) index. The isTableCell flag is set to true.
     */
    public SlideObject(String id, int rowIndex, int columnIndex) {
        this(id, rowIndex, columnIndex, true);
    }
}
