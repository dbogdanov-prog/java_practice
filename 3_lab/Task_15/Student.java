import java.util.Set;

public record Student(String name, int age, Set<String> completed, boolean paid, boolean scholarship, boolean blocked) {
}
