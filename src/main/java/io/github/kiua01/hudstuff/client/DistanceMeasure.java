package io.github.kiua01.hudstuff.client;

import java.util.Locale;

/** Two-point distance measure tool (like Bedrock distance_measure_tool). */
public final class DistanceMeasure {
    public static boolean hasA = false;
    public static boolean hasB = false;
    public static double ax, ay, az;
    public static double bx, by, bz;

    private DistanceMeasure() {}

    public static void setA(double x, double y, double z) {
        ax = x; ay = y; az = z;
        hasA = true;
    }

    public static void setB(double x, double y, double z) {
        bx = x; by = y; bz = z;
        hasB = true;
    }

    public static void clear() {
        hasA = hasB = false;
    }

    public static String line() {
        if (!hasA) return "Measure: set A with .measure a";
        if (!hasB) {
            return String.format(Locale.ROOT, "Measure A: %.1f %.1f %.1f  (set B with .measure b)", ax, ay, az);
        }
        double dx = bx - ax, dy = by - ay, dz = bz - az;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double horiz = Math.sqrt(dx * dx + dz * dz);
        return String.format(Locale.ROOT,
            "Measure: %.1fm  (H %.1f  dY %.1f)  A(%.0f %.0f %.0f)->B(%.0f %.0f %.0f)",
            dist, horiz, dy, ax, ay, az, bx, by, bz);
    }
}
