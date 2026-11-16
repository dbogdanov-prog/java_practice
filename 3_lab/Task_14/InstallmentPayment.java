public class InstallmentPayment implements PaymentPolicy {
    @Override
    public double total(double basePrice, int months) { return basePrice * 1.05; }

    @Override
    public String label() { return "рассрочка (+5%)"; }
}
