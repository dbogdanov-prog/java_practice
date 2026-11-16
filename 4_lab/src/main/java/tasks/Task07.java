package tasks;

import coursehub.Course;

import java.util.Map;
import java.util.Optional;

/*
 * Выбор:
 *  findCourse     -> Optional            (курса может не быть, это нормально)
 *  parseCourse    -> типизированный result (много пользовательских строк неверны, это ожидаемо)
 *  enroll         -> unchecked exception (EnrollmentRejectedException) с кодом причины
 *  calculatePrice -> unchecked exception (IllegalArgumentException), плохие аргументы = ошибка вызывающего
 *
 * Ниже реализованы два контрастных случая: findCourse и calculatePrice.
 */
public class Task07 {

    private static final Map<String, Course> COURSES = Map.of("JAVA-101", new Course("Java", 40));

    /** Ожидаемый результат: «не найдено» это не ошибка. */
    public static Optional<Course> findCourse(String code) {
        return Optional.ofNullable(COURSES.get(code));
    }

    /** Нарушение контракта: плохие аргументы = исключение. */
    public static int calculatePrice(int hours, int pricePerHour) {
        if (hours <= 0 || pricePerHour < 0) {
            throw new IllegalArgumentException("hours must be > 0 and price must be >= 0");
        }
        return hours * pricePerHour;
    }
}