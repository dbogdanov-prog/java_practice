// открытая стратегия: реализовать может любой
public interface EnrollmentRule {
    RuleResult check(EnrollmentRequest request);

    static EnrollmentRule allOf(EnrollmentRule... rules) { return new AllOf(rules); }

    static EnrollmentRule anyOf(EnrollmentRule... rules) { return new AnyOf(rules); }
}
