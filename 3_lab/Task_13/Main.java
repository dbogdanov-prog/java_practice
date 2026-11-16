public class Main {
    public static void main(String[] args) {
        String text = "Курс начинается завтра в 10:00";

        System.out.println(new NotificationBefore("sms").send("+7000", text));

        Notifier[] notifiers = {
                NotifierFactory.create("email", "noreply@coursehub.io"),
                NotifierFactory.create("email", "support@coursehub.io"),
                NotifierFactory.create("sms", null),
                NotifierFactory.create("push", null)
        };
        for (Notifier n : notifiers) {
            System.out.println(n.send("user", text));
        }
    }
}
