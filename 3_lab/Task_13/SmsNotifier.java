public class SmsNotifier implements Notifier {
    private static final int LIMIT = 20;

    @Override
    public String send(String to, String text) {
        return "SMS -> " + to + ": " + text.substring(0, Math.min(text.length(), LIMIT));
    }
}
