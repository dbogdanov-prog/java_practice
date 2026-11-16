// закрытая иерархия: Pass, Fail, Defer
public sealed interface RuleResult {
    record Pass() implements RuleResult {
    }

    record Fail(String reason) implements RuleResult {
    }

    record Defer(String reason) implements RuleResult {
    }
}
