import java.util.List;
import java.util.Set;

public class Main {
    static String show(EnrollmentDecision d) {
        return switch (d) {
            case EnrollmentDecision.Accepted a -> a.student() + ": принят";
            case EnrollmentDecision.Rejected r -> r.student() + ": отказ (" + r.reason() + ")";
            case EnrollmentDecision.WaitListed w -> w.student() + ": лист ожидания (" + w.reason() + ")";
        };
    }

    public static void main(String[] args) {
        Set<String> basics = Set.of("Java Basics");
        Course open = new Course("Java Advanced", 16, basics, 30, 10);
        Course full = new Course("Java Advanced", 16, basics, 10, 10);

        Student anna = new Student("Анна", 20, basics, true, false, false);
        Student boris = new Student("Боря", 14, basics, true, false, false);
        Student vera = new Student("Вера", 22, Set.of(), false, false, false);
        Student gleb = new Student("Глеб", 19, basics, false, true, false);
        Student egor = new Student("Егор", 25, basics, true, false, true);

        System.out.println("== Правила по отдельности (dynamic dispatch через EnrollmentRule)");
        List<EnrollmentRule> rules = List.of(new NotBlockedRule(), new AgeRule(), new PrerequisiteRule(),
                new PaymentRule(), new ScholarshipRule(), new CapacityRule());
        EnrollmentRequest request = new EnrollmentRequest(vera, full);
        for (EnrollmentRule rule : rules) {
            System.out.println(rule.getClass().getSimpleName() + " -> " + rule.check(request));
        }

        EnrollmentRule engine = EnrollmentRule.allOf(
                new NotBlockedRule(),
                new AgeRule(),
                new PrerequisiteRule(),
                EnrollmentRule.anyOf(new PaymentRule(), new ScholarshipRule()),
                new CapacityRule());
        EnrollmentService service = new EnrollmentService(engine);

        System.out.println("== Движок: allOf(..., anyOf(Payment, Scholarship), Capacity)");
        for (Student s : List.of(anna, boris, vera, gleb, egor)) {
            System.out.println(show(service.enroll(s, open)));
        }
        System.out.println("-- курс без мест");
        System.out.println(show(service.enroll(anna, full)));
        System.out.println(show(service.enroll(boris, full)));

        System.out.println("== Новое правило без изменения сервиса");
        EnrollmentRule onlyEvenAge = r -> r.student().age() % 2 == 0
                ? new RuleResult.Pass() : new RuleResult.Fail("нечётный возраст");
        EnrollmentService custom = new EnrollmentService(EnrollmentRule.allOf(engine, onlyEvenAge));
        System.out.println(show(custom.enroll(anna, open)));
        System.out.println(show(custom.enroll(gleb, open)));
    }
}
