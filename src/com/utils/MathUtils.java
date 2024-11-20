package com.utils;

public class MathUtils {
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Number must be non-negative.");
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static double squareRoot(double x) {
        if (x < 0) throw new IllegalArgumentException("Number must be non-negative.");
        return Math.sqrt(x);
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }
}
