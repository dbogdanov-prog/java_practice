package coursehub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Почему одного «обычного» примера (например, 40 часов) мало:
 * он пройдёт и при «>=» вместо «>», и при диапазоне 1..1000 вместо 8..200.
 * Только значения вокруг границ (7, 8, 200, 201) фиксируют настоящий контракт
 * и ловят ошибки «на единицу».
 */
class CourseDurationBoundaryTest {

    @ParameterizedTest(name = "{0} часов допустимо")
    @ValueSource(ints = {Course.MIN_HOURS, Course.MAX_HOURS}) // 8 и 200
    void validBoundaries_areAccepted(int hours) {
        Course course = new Course("Java", hours);

        assertEquals(hours, course.getHours());
    }

    @ParameterizedTest(name = "{0} часов недопустимо")
    @ValueSource(ints = {Course.MIN_HOURS - 1, Course.MAX_HOURS + 1}) // 7 и 201
    void outsideBoundaries_areRejected(int hours) {
        assertThrows(IllegalArgumentException.class, () -> new Course("Java", hours));
    }

    @Test
    void usualValue_isAccepted() {
        assertDoesNotThrow(() -> new Course("Java", 40));
    }
}