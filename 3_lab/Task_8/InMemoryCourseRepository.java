import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<Integer, Course> data = new LinkedHashMap<>();

    @Override
    public void save(Course course) { data.put(course.id(), course); }

    @Override
    public Optional<Course> findById(int id) { return Optional.ofNullable(data.get(id)); }

    @Override
    public List<Course> findAll() { return List.copyOf(data.values()); }
}
