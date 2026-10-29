package io.github.kiua01.hudstuff.client;

/**
 * Feature toggles aligned with Bedrock HUD Essentials settings.
 * Defaults mirror the common "everything useful on" setup.
 */
public final class HudConfig {
    // Master
    public static boolean enabled = true;

    // Core position / world
    public static boolean showCoords = true;
    public static boolean showFacing = true;
    public static boolean showBiome = true;
    public static boolean showLight = true;
    public static boolean showChunk = true;
    public static boolean showTime = true;
    public static boolean showFps = true;
    public static boolean showSpeed = true;
    public static boolean showMoon = true;

    // Player status
    public static boolean showArmor = true;
    public static boolean showXp = true;
    public static boolean showHeldItem = true;
    public static boolean showArrowCount = true;
    public static boolean inventoryFullWarn = true;

    // Death / waypoints
    public static boolean showDeath = true;
    public static boolean showWaypoint = true;
    public static boolean chunkBorders = false;
    public static boolean showSlime = true;
    public static boolean showLookAt = true;
    public static boolean showCrop = true;
    public static boolean showRedstone = true;
    public static boolean showBlockCount = true;
    public static boolean showEntity = true;
    public static boolean showHoney = true;
    public static boolean showMeasure = true;
    public static boolean showWaypoints = true;

    public static int color = 0xE0E0E0;
    public static int warnColor = 0xFF5555;

    // Last death
    public static boolean hasDeath = false;
    public static double deathX, deathY, deathZ;
    public static String deathDim = "";

    // Custom waypoint (one active marker — full multi-waypoint UI later)
    public static boolean hasWaypoint = false;
    public static double wpX, wpY, wpZ;
    public static String wpDim = "";
    public static String wpName = "Waypoint";

    private HudConfig() {}

    public static void toggle() {
        enabled = !enabled;
    }

    public static void setDeath(double x, double y, double z, String dim) {
        hasDeath = true;
        deathX = x; deathY = y; deathZ = z;
        deathDim = dim == null ? "" : dim;
    }

    public static void clearDeath() {
        hasDeath = false;
    }

    public static void setWaypoint(double x, double y, double z, String dim, String name) {
        hasWaypoint = true;
        wpX = x; wpY = y; wpZ = z;
        wpDim = dim == null ? "" : dim;
        wpName = name == null || name.isBlank() ? "Waypoint" : name;
    }

    public static void clearWaypoint() {
        hasWaypoint = false;
    }
}
