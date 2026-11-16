public class FixedPayment implements PaymentPolicy {
    @Override
    public double total(double basePrice, int months) { return basePrice; }

    @Override
    public String label() { return "фиксированная цена"; }
}
