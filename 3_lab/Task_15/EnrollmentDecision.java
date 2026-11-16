public sealed interface EnrollmentDecision {
    record Accepted(String student) implements EnrollmentDecision {
    }

    record Rejected(String student, String reason) implements EnrollmentDecision {
    }

    record WaitListed(String student, String reason) implements EnrollmentDecision {
    }
}
