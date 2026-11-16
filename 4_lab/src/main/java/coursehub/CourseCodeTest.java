package coursehub;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseCodeTest {

    @ParameterizedTest(name = "[{0}] -> {1}")
    @CsvSource({
            "cs-101, CS-101",
            "'  cs-101  ', CS-101",
            "Math-200, MATH-200",
            "JAVA-999, JAVA-999"
    })
    void of_normalizesCode(String input, String expected) {
        assertEquals(expected, CourseCode.of(input).value());
    }

    @ParameterizedTest(name = "[{0}] отклоняется")
    @NullSource
    @ValueSource(strings = {"", "   ", "C-101", "CS101", "CS-10", "CS-1234", "12-345", "CS_101", "ABCDE-101"})
    void of_rejectsInvalidCode(String input) {
        assertThrows(IllegalArgumentException.class, () -> CourseCode.of(input));
    }
}