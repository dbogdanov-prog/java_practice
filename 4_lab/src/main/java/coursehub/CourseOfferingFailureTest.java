package coursehub;

import coursehub.EnrollmentRejectedException.Reason;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseOfferingFailureTest {

    @Test
    void enroll_whenNoSeats_rejectsAndKeepsStateUnchanged() {
        // Arrange
        CourseOffering offering = new CourseOffering("JAVA-101", 1, null);
        offering.enroll("s1", Set.of()); // подготовка вне lambda

        // Act: в lambda ровно одно действие
        EnrollmentRejectedException ex = assertThrows(
                EnrollmentRejectedException.class,
                () -> offering.enroll("s2", Set.of()));

        // Assert: исключение
        assertEquals(Reason.NO_SEATS, ex.getReason());
        assertEquals("s2", ex.getStudentId());
        assertEquals("JAVA-101", ex.getCourseId());
        assertFalse(ex.getMessage().contains("s2")); // id студента не раскрыт

        // Assert: объект не изменился
        assertEquals(1, offering.enrolledCount());
        assertTrue(offering.isEnrolled("s1"));
        assertFalse(offering.isEnrolled("s2"));
    }

    @Test
    void enroll_whenCourseClosed_rejectsAndKeepsStateUnchanged() {
        CourseOffering offering = new CourseOffering("JAVA-101", 5, null);
        offering.close();

        EnrollmentRejectedException ex = assertThrows(
                EnrollmentRejectedException.class,
                () -> offering.enroll("s1", Set.of()));

        assertEquals(Reason.COURSE_CLOSED, ex.getReason());
        assertEquals(0, offering.enrolledCount());
    }
}