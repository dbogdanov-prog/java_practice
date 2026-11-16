package coursehub;

import java.util.Objects;

public class EnrollmentRejectedException extends RuntimeException {

    public enum Reason {
        COURSE_NOT_FOUND, COURSE_CLOSED, NO_SEATS, PREREQUISITE_MISSING, ALREADY_ENROLLED
    }

    private final String studentId;
    private final String courseId;
    private final Reason reason;

    public EnrollmentRejectedException(String studentId, String courseId, Reason reason) {
        this(studentId, courseId, reason, null);
    }

    /** Этот конструктор нужен, чтобы сохранить исходную причину (cause) при оборачивании. */
    public EnrollmentRejectedException(String studentId, String courseId, Reason reason, Throwable cause) {
        super("Enrollment rejected: " + Objects.requireNonNull(reason), cause);
        this.studentId = studentId;
        this.courseId = courseId;
        this.reason = reason;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public Reason getReason() {
        return reason;
    }

    // В сообщении и toString нет studentId и courseId, чтобы не раскрывать лишнее
    @Override
    public String toString() {
        return "EnrollmentRejectedException[reason=" + reason + "]";
    }
}