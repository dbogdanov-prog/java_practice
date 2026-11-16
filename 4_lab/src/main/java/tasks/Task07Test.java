package tasks;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class Task07Test {

    @Test
    void findCourse_unknownCode_returnsEmptyAndDoesNotThrow() {
        Optional<?> result = Task07.findCourse("NOPE-999");

        assertTrue(result.isEmpty());
    }

    @Test
    void findCourse_knownCode_returnsCourse() {
        assertTrue(Task07.findCourse("JAVA-101").isPresent());
    }

    @Test
    void calculatePrice_negativeHours_throws() {
        assertThrows(IllegalArgumentException.class, () -> Task07.calculatePrice(-1, 10));
    }

    @Test
    void calculatePrice_validInput_returnsProduct() {
        assertEquals(400, Task07.calculatePrice(40, 10));
    }
}