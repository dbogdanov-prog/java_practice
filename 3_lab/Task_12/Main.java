import java.util.List;

public class Main {
    static String describe(EnrollmentResult result) {
        return switch (result) {
            case Accepted a -> a.student() + ": принят";
            case Rejected r -> r.student() + ": отказ (" + r.reason() + ")";
            case WaitListed w -> w.student() + ": в листе ожидания, место " + w.position();
            case Cancelled c -> c.student() + ": запись отменена";
        };
    }

    public static void main(String[] args) {
        List<EnrollmentResult> results = List.of(
                new Accepted("Анна"),
                new Rejected("Боря", "возраст"),
                new WaitListed("Вера", 3),
                new Cancelled("Глеб"));
        results.forEach(r -> System.out.println(describe(r)));
    }
}
