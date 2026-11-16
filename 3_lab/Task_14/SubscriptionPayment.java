public class SubscriptionPayment implements PaymentPolicy {
    @Override
    public double total(double basePrice, int months) { return basePrice * months; }

    @Override
    public String label() { return "подписка"; }
}
