import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Course course = new Course("Java Advanced", 16, Set.of("Java Basics"), 2, 1);
        Student ok = new Student("Анна", 20, Set.of("Java Basics"));
        Student young = new Student("Боря", 14, Set.of("Java Basics"));
        Student noPrereq = new Student("Вера", 22, Set.of());

        EnrollmentService service = new EnrollmentService(
                List.of(new AgePolicy(), new PrerequisitePolicy(), new CapacityPolicy()));
        for (Student s : List.of(ok, young, noPrereq)) {
            System.out.println(s.name() + ": " + service.check(s, course).orElse("допущен"));
        }

        // новая политика — без изменения EnrollmentService
        EnrollmentPolicy noBlocked = (s, c) ->
                s.name().equals("Анна") ? Optional.of("Студент в чёрном списке") : Optional.empty();
        EnrollmentService extended = new EnrollmentService(
                List.of(new AgePolicy(), new PrerequisitePolicy(), new CapacityPolicy(), noBlocked));
        System.out.println("С новой политикой: " + ok.name() + ": "
                + extended.check(ok, course).orElse("допущен"));
    }
}
