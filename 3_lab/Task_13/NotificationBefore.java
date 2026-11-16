// исходный код с type code
public class NotificationBefore {
    private final String notificationType;

    public NotificationBefore(String notificationType) { this.notificationType = notificationType; }

    public String send(String to, String text) {
        switch (notificationType) {
            case "email": return "Email -> " + to + ": " + text;
            case "sms": return "SMS -> " + to + ": " + text.substring(0, Math.min(text.length(), 20));
            case "push": return "Push -> " + to + ": " + text;
            default: throw new IllegalArgumentException(notificationType);
        }
    }
}
