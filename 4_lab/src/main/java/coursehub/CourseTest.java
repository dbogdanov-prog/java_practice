package coursehub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseTest {

    @Test
    void completeHours_returnsRemainingHours_whenSomeHoursAreDone() {
        // Arrange
        Course course = new Course("Java", 40);

        // Act
        int remaining = course.completeHours(10);

        // Assert
        assertEquals(30, remaining);
    }
}