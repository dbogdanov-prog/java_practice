import java.util.Set;

public record Course(String title, int minAge, Set<String> prerequisites, int capacity, int enrolled) {
}
