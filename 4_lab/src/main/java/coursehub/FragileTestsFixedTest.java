package coursehub;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Что было плохо и как исправлено:
 *
 * 1) Зависимость от порядка и общий static state:
 *      было:  static CourseOffering OFFERING = ...;  + @TestMethodOrder(OrderAnnotation)
 *      стало: каждый тест создаёт СВОЙ объект; порядок не важен
 *             (в junit-platform.properties включён случайный порядок).
 *
 * 2) Зависимость от текущей директории:
 *      было:  Path.of("courses.csv")
 *      стало: @TempDir: у каждого теста своя временная папка.
 *
 * 3) Точный полный текст toString:
 *      было:  assertEquals("Course[Java, 40h]", course.toString());
 *      стало: проверяем только важные части (название и часы).
 */
class FragileTestsFixedTest {

    // Два теста ниже раньше делили один static-объект и ломались друг об друга.
    @Test
    void firstTest_ownOffering_canEnrollS1() {
        CourseOffering offering = new CourseOffering("JAVA-101", 1, null);

        offering.enroll("s1", Set.of());

        assertEquals(1, offering.enrolledCount());
    }

    @Test
    void secondTest_ownOffering_canAlsoEnrollS1() {
        CourseOffering offering = new CourseOffering("JAVA-101", 1, null);

        offering.enroll("s1", Set.of());

        assertTrue(offering.isEnrolled("s1"));
    }

    @Test
    void readCourseFromTempFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("courses.csv");
        Files.writeString(file, "Java;40\n");

        List<String> lines = Files.readAllLines(file);
        String[] parts = lines.get(0).split(";");
        Course course = new Course(parts[0], Integer.parseInt(parts[1]));

        assertEquals("Java", course.getTitle());
        assertEquals(40, course.getHours());
    }

    @Test
    void toString_containsTitleAndHours() {
        Course course = new Course("Java", 40);

        String text = course.toString();

        assertTrue(text.contains("Java"));
        assertTrue(text.contains("40"));
    }
}