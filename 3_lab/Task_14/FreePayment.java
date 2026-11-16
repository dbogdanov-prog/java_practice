public class FreePayment implements PaymentPolicy {
    @Override
    public double total(double basePrice, int months) { return 0; }

    @Override
    public String label() { return "бесплатно"; }
}
