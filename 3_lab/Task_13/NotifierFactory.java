public class NotifierFactory {
    // единственное место, где строка из конфигурации превращается в объект
    public static Notifier create(String type, String sender) {
        return switch (type) {
            case "email" -> new EmailNotifier(sender);
            case "sms" -> new SmsNotifier();
            case "push" -> new PushNotifier();
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        };
    }
}
