package com.example.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link DiscountCalculator}.
 *
 * Test categories:
 *  1. Standard cases   — representative values within each tier
 *  2. Boundary cases   — values at and adjacent to every tier boundary
 *  3. Exception cases  — negative inputs must throw IllegalArgumentException
 */
@DisplayName("DiscountCalculator — Full Test Suite")
class DiscountCalculatorTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // ════════════════════════════════════════════════════════════════════════
    // 1. STANDARD CASES
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("1. Standard Cases")
    class StandardCases {

        @Test
        @DisplayName("$50.00 → 0% discount → final amount $50.00")
        void standardTier1_50dollars() {
            double result = calculator.calculateDiscount(50.00);
            assertEquals(50.00, result, 0.0001,
                    "No discount should be applied for $50.00");
        }

        @Test
        @DisplayName("$200.00 → 10% discount → final amount $180.00")
        void standardTier2_200dollars() {
            double result = calculator.calculateDiscount(200.00);
            assertEquals(180.00, result, 0.0001,
                    "10% discount should be applied for $200.00");
        }

        @Test
        @DisplayName("$1000.00 → 20% discount → final amount $800.00")
        void standardTier3_1000dollars() {
            double result = calculator.calculateDiscount(1000.00);
            assertEquals(800.00, result, 0.0001,
                    "20% discount should be applied for $1000.00");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. BOUNDARY / EDGE CASES
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("2. Boundary / Edge Cases")
    class BoundaryCases {

        // ── Tier 1 boundaries ($0.00 – $99.99) ──────────────────────────────

        @Test
        @DisplayName("Boundary: $0.00 (lowest valid) → 0% discount → $0.00")
        void boundary_exactlyZero() {
            double result = calculator.calculateDiscount(0.00);
            assertEquals(0.00, result, 0.0001,
                    "$0.00 is the lowest valid amount; no discount applies");
        }

        @Test
        @DisplayName("Boundary: $99.99 (top of Tier 1) → 0% discount → $99.99")
        void boundary_topOfTier1() {
            double result = calculator.calculateDiscount(99.99);
            assertEquals(99.99, result, 0.0001,
                    "$99.99 is still Tier 1; no discount should apply");
        }

        // ── Tier 1 → Tier 2 transition ($99.99 / $100.00) ───────────────────

        @Test
        @DisplayName("Boundary: $100.00 (first Tier 2 value) → 10% → $90.00")
        void boundary_firstTier2() {
            double result = calculator.calculateDiscount(100.00);
            assertEquals(90.00, result, 0.0001,
                    "$100.00 is the start of Tier 2; 10% discount must apply");
        }

        // ── Tier 2 boundaries ($100.00 – $499.99) ───────────────────────────

        @Test
        @DisplayName("Boundary: $499.99 (top of Tier 2) → 10% → $449.991")
        void boundary_topOfTier2() {
            double result = calculator.calculateDiscount(499.99);
            assertEquals(449.991, result, 0.0001,
                    "$499.99 is still Tier 2; 10% discount should apply");
        }

        // ── Tier 2 → Tier 3 transition ($499.99 / $500.00) ──────────────────

        @Test
        @DisplayName("Boundary: $500.00 (first Tier 3 value) → 20% → $400.00")
        void boundary_firstTier3() {
            double result = calculator.calculateDiscount(500.00);
            assertEquals(400.00, result, 0.0001,
                    "$500.00 is the start of Tier 3; 20% discount must apply");
        }

        // ── Parameterized sweep across all key boundary values ───────────────

        /**
         * Condensed parameterized test covering all six boundary amounts
         * in a single, readable table.
         *
         * Columns: purchaseAmount | expectedResult | label
         */
        @ParameterizedTest(name = "[{index}] {2}: ${0} → ${1}")
        @DisplayName("Parameterized boundary sweep")
        @CsvSource({
            // amount,  expected,   label
            "0.00,      0.00,       'Zero — start of Tier 1'",
            "99.99,     99.99,      'Top of Tier 1 (just below 10% threshold)'",
            "100.00,    90.00,      'Start of Tier 2 (10% kicks in)'",
            "499.99,    449.991,    'Top of Tier 2 (just below 20% threshold)'",
            "500.00,    400.00,     'Start of Tier 3 (20% kicks in)'",
            "1000.00,   800.00,     'Mid-Tier-3 representative value'"
        })
        void parameterizedBoundarySweep(double amount, double expected,
                                        String label) {
            double result = calculator.calculateDiscount(amount);
            assertEquals(expected, result, 0.0001, label);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. EXCEPTION CASES — Negative inputs
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("3. Exception Cases — Negative Inputs")
    class ExceptionCases {

        @Test
        @DisplayName("Exception: -0.01 (just below zero) → IllegalArgumentException")
        void exception_justBelowZero() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDiscount(-0.01),
                    "Values just below zero must throw IllegalArgumentException"
            );
            assertEquals("Purchase amount cannot be negative", ex.getMessage());
        }

        @Test
        @DisplayName("Exception: -1.00 → IllegalArgumentException")
        void exception_negativeDollar() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDiscount(-1.00)
            );
            assertEquals("Purchase amount cannot be negative", ex.getMessage());
        }

        @Test
        @DisplayName("Exception: -999.99 (large negative) → IllegalArgumentException")
        void exception_largeNegative() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDiscount(-999.99),
                    "Large negative values must also throw IllegalArgumentException"
            );
        }

        @Test
        @DisplayName("Exception: Double.NEGATIVE_INFINITY → IllegalArgumentException")
        void exception_negativeInfinity() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateDiscount(Double.NEGATIVE_INFINITY)
            );
        }
    }
}
