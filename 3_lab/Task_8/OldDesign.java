import java.util.ArrayList;
import java.util.List;

// исходный дизайн с нарушением LSP
class OldCourseRepository {
    private final List<Course> data = new ArrayList<>();

    public void save(Course course) { data.add(course); }

    public List<Course> findAll() { return data; }
}

class OldReadOnlyCourseRepository extends OldCourseRepository {
    @Override
    public void save(Course course) {
        throw new UnsupportedOperationException("read-only");
    }
}
