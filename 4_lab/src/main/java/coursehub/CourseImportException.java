package coursehub;

public class CourseImportException extends RuntimeException {
    private final int recordNumber;

    public CourseImportException(int recordNumber, String reason, Throwable cause) {
        super("Import failed at record " + recordNumber + ": " + reason, cause);
        this.recordNumber = recordNumber;
    }

    public int getRecordNumber() {
        return recordNumber;
    }
}