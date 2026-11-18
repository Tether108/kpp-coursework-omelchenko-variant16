package ua.nupp.defects.service;


public class IndexCalculator {
    public static final double MAX_AREA = 25.0;

    public static double severityFactor(int severity) {
        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException("Тяжкість дефекту має бути від 1 до 5");
        }
        return (severity - 1) * 25.0;
    }

    public static double areaFactor(double area) {
        if (area < 0) {
            throw new IllegalArgumentException("Площа дефекту не може бути від'ємною");
        }
        double value = area / MAX_AREA * 100.0;
        return round(Math.min(100.0, value));
    }

    public static double safetyFactor(int safetyScore) {
        if (safetyScore < 0 || safetyScore > 100) {
            throw new IllegalArgumentException("Оцінка впливу на безпеку має бути від 0 до 100");
        }
        return safetyScore;
    }

    public static double calculate(double f1, double f2, double f3) {
        checkFactor(f1, "F1");
        checkFactor(f2, "F2");
        checkFactor(f3, "F3");
        return round(0.40 * f1 + 0.35 * f2 + 0.25 * f3);
    }

    private static void checkFactor(double value, String name) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(name + " має бути в діапазоні 0..100");
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
