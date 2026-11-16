package coursehub;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** Открытый набор на курс: сюда записывают студентов. */
public class CourseOffering {
    private final String courseId;
    private final int capacity;
    private final String prerequisite; // может быть null
    private boolean open = true;
    private final Set<String> enrolled = new LinkedHashSet<>();

    public CourseOffering(String courseId, int capacity, String prerequisite) {
        this.courseId = courseId;
        this.capacity = capacity;
        this.prerequisite = prerequisite;
    }

    public void close() {
        open = false;
    }

    public int enrolledCount() {
        return enrolled.size();
    }

    public boolean isEnrolled(String studentId) {
        return enrolled.contains(studentId);
    }

    public void enroll(String studentId, Set<String> completedCourses) {
        Objects.requireNonNull(studentId, "studentId");
        Objects.requireNonNull(completedCourses, "completedCourses");

        // Порядок проверок = приоритет правил в таблице решений (задание 13)
        if (!open) {
            throw rejected(studentId, EnrollmentRejectedException.Reason.COURSE_CLOSED);
        }
        if (enrolled.contains(studentId)) {
            throw rejected(studentId, EnrollmentRejectedException.Reason.ALREADY_ENROLLED);
        }
        if (prerequisite != null && !completedCourses.contains(prerequisite)) {
            throw rejected(studentId, EnrollmentRejectedException.Reason.PREREQUISITE_MISSING);
        }
        if (enrolled.size() >= capacity) {
            throw rejected(studentId, EnrollmentRejectedException.Reason.NO_SEATS);
        }
        enrolled.add(studentId); // состояние меняем только в самом конце
    }

    private EnrollmentRejectedException rejected(String studentId, EnrollmentRejectedException.Reason reason) {
        return new EnrollmentRejectedException(studentId, courseId, reason);
    }
}