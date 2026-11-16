public abstract class AbstractNotificationSender {
    public final void send(String to, String message) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Получатель не указан");
        }
        doSend(to, message);
    }

    protected abstract void doSend(String to, String message);
}
