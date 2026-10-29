package io.github.kiua01.hudstuff.client;

/** In-memory config for HUD Stuff. */
public final class HudConfig {
    public static boolean enabled = true;
    public static boolean showCoords = true;
    public static boolean showFacing = true;
    public static boolean showBiome = true;
    public static boolean showLight = true;
    public static boolean showChunk = true;
    public static boolean showTime = true;
    public static boolean showDeath = true;
    public static boolean chunkBorders = true;
    public static int color = 0xE0E0E0;

    public static boolean hasDeath = false;
    public static double deathX, deathY, deathZ;
    public static String deathDim = "";

    private HudConfig() {}

    public static void toggle() {
        enabled = !enabled;
    }

    public static void setDeath(double x, double y, double z, String dim) {
        hasDeath = true;
        deathX = x;
        deathY = y;
        deathZ = z;
        deathDim = dim == null ? "" : dim;
    }

    public static void clearDeath() {
        hasDeath = false;
    }
}
