public class Main {
    public static void main(String[] args) {
        System.out.println("Порядок A: Logging(Metrics(Retry(Email)))");
        MetricsSender metricsA = new MetricsSender(new RetrySender(new FlakyEmailSender(2), 3));
        NotificationSender a = new LoggingSender(metricsA);
        a.send("user@mail.com", "Привет");
        System.out.println("  metrics: " + metricsA.report());

        System.out.println("Порядок B: Retry(Logging(Metrics(Email)))");
        MetricsSender metricsB = new MetricsSender(new FlakyEmailSender(2));
        NotificationSender b = new RetrySender(new LoggingSender(metricsB), 3);
        b.send("user@mail.com", "Привет");
        System.out.println("  metrics: " + metricsB.report());
    }
}
