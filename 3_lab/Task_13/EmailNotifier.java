public class EmailNotifier implements Notifier {
    private final String from;

    public EmailNotifier(String from) { this.from = from; }

    @Override
    public String send(String to, String text) {
        return "Email " + from + " -> " + to + ": " + text;
    }
}
