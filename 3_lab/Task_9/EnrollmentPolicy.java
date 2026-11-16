import java.util.Optional;

public interface EnrollmentPolicy {
    // пусто — допуск разрешён, иначе причина отказа
    Optional<String> reject(Student student, Course course);
}
