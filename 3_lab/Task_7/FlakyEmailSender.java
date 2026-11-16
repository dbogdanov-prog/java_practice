public class FlakyEmailSender implements NotificationSender {
    private final int failTimes;
    private int attempts;

    public FlakyEmailSender(int failTimes) { this.failTimes = failTimes; }

    @Override
    public void send(String to, String message) {
        attempts++;
        if (attempts <= failTimes) {
            throw new IllegalStateException("smtp недоступен");
        }
        System.out.println("  Email -> " + to + ": " + message);
    }
}
