import java.util.List;
import java.util.Optional;

public interface CourseReader {
    Optional<Course> findById(int id);

    List<Course> findAll();
}
