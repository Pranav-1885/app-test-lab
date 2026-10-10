package com.example.calculator;

/**
 * DiscountCalculator — applies a tiered discount to a purchase amount.
 *
 * <pre>
 * | Purchase Amount        | Discount |
 * |------------------------|----------|
 * | Below $0.00            | throws IllegalArgumentException |
 * | $0.00  – $99.99        | 0 %      |
 * | $100.00 – $499.99      | 10 %     |
 * | $500.00 and above      | 20 %     |
 * </pre>
 */
public class DiscountCalculator {

    // ── Discount Thresholds ──────────────────────────────────────────────────
    private static final double TIER_2_MIN = 100.00;   // 10 % starts here
    private static final double TIER_3_MIN = 500.00;   // 20 % starts here

    // ── Discount Rates ───────────────────────────────────────────────────────
    private static final double RATE_TIER_1 = 0.00;    //  0 %
    private static final double RATE_TIER_2 = 0.10;    // 10 %
    private static final double RATE_TIER_3 = 0.20;    // 20 %

    /**
     * Calculates the discounted price for the given purchase amount.
     *
     * @param purchaseAmount the original purchase amount (must be >= 0)
     * @return the amount after the applicable discount has been applied
     * @throws IllegalArgumentException if {@code purchaseAmount} is negative
     */
    public double calculateDiscount(double purchaseAmount) {
        if (purchaseAmount < 0) {
            throw new IllegalArgumentException(
                    "Purchase amount cannot be negative");
        }

        double discountRate;

        if (purchaseAmount >= TIER_3_MIN) {
            discountRate = RATE_TIER_3;          // 20 %
        } else if (purchaseAmount >= TIER_2_MIN) {
            discountRate = RATE_TIER_2;          // 10 %
        } else {
            discountRate = RATE_TIER_1;          //  0 %
        }

        return purchaseAmount * (1.0 - discountRate);
    }
}
