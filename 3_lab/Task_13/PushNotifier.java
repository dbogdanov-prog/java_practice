public class PushNotifier implements Notifier {
    @Override
    public String send(String to, String text) {
        return "Push -> " + to + ": " + text;
    }
}
