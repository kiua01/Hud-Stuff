package io.github.kiua01.hudstuff.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Up to 15 named waypoints (matches Bedrock HUD Essentials capacity). */
public final class Waypoints {
    public static final int MAX = 15;

    public static final class Entry {
        public final String name;
        public final double x, y, z;
        public final String dim;

        public Entry(String name, double x, double y, double z, String dim) {
            this.name = name == null || name.isBlank() ? "WP" : name;
            this.x = x; this.y = y; this.z = z;
            this.dim = dim == null ? "" : dim;
        }

        public double distanceTo(double px, double py, double pz) {
            double dx = x - px, dy = y - py, dz = z - pz;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s (%.0f %.0f %.0f)", name, x, y, z);
        }
    }

    private static final List<Entry> LIST = new ArrayList<>();

    private Waypoints() {}

    public static List<Entry> all() {
        return List.copyOf(LIST);
    }

    public static int size() {
        return LIST.size();
    }

    public static boolean add(String name, double x, double y, double z, String dim) {
        if (LIST.size() >= MAX) return false;
        LIST.add(new Entry(name, x, y, z, dim));
        return true;
    }

    public static boolean remove(int index1Based) {
        int i = index1Based - 1;
        if (i < 0 || i >= LIST.size()) return false;
        LIST.remove(i);
        return true;
    }

    public static void clear() {
        LIST.clear();
    }
}
