package coursehub;

import coursehub.EnrollmentRejectedException.Reason;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ТАБЛИЦА РЕШЕНИЙ (условия проверяются сверху вниз, как в CourseOffering.enroll):
 *
 * Правило | курс закрыт | уже записан | prerequisite выполнен | есть места | результат
 * --------+-------------+-------------+-----------------------+------------+-------------------
 *   R1    |     да      |      -      |           -           |     -      | COURSE_CLOSED
 *   R2    |     нет     |     да      |           -           |     -      | ALREADY_ENROLLED
 *   R3    |     нет     |     нет     |          нет          |     -      | PREREQUISITE_MISSING
 *   R4    |     нет     |     нет     |          да           |    нет     | NO_SEATS
 *   R5    |     нет     |     нет     |          да           |    да      | успех
 *
 * "-" означает "не важно". Из 16 комбинаций осталось 5 существенных правил.
 * Остальные комбинации дублируют эти правила и покрытие не увеличивают, поэтому их нет.
 */
class EnrollmentDecisionTableTest {

    @ParameterizedTest(name = "{0}: closed={1}, seats={2}, already={3}, prereq={4} -> {5}")
    @CsvSource({
            "R1, true,  true,  false, true,  COURSE_CLOSED",
            "R2, false, true,  true,  true,  ALREADY_ENROLLED",
            "R3, false, true,  false, false, PREREQUISITE_MISSING",
            "R4, false, false, false, true,  NO_SEATS",
            "R5, false, true,  false, true,  SUCCESS"
    })
    void decisionTable(String rule, boolean closed, boolean hasSeats, boolean already,
                       boolean prereq, String expected) {
        // Arrange
        CourseOffering offering = new CourseOffering("JAVA-101", 2, "BASICS-100");
        Set<String> done = prereq ? Set.of("BASICS-100") : Set.of();
        if (already) {
            offering.enroll("s1", Set.of("BASICS-100"));
        }
        for (int i = 0; !hasSeats && offering.enrolledCount() < 2; i++) {
            offering.enroll("other" + i, Set.of("BASICS-100"));
        }
        if (closed) {
            offering.close();
        }

        // Act + Assert
        if (expected.equals("SUCCESS")) {
            offering.enroll("s1", done);
            assertTrue(offering.isEnrolled("s1"));
        } else {
            EnrollmentRejectedException ex = assertThrows(
                    EnrollmentRejectedException.class,
                    () -> offering.enroll("s1", done));
            assertEquals(Reason.valueOf(expected), ex.getReason());
        }
    }
}