package io.github.kiua01.hudstuff.client;

/** Simple in-memory config for the HUD overlay. */
public final class HudConfig {
    public static boolean enabled = true;
    public static boolean showCoords = true;
    public static boolean showFacing = true;
    public static boolean showBiome = true;
    public static boolean showLight = true;
    public static boolean showChunk = true;
    public static boolean showTime = true;
    public static int color = 0xE0E0E0;
    public static int shadowColor = 0x80000000;

    private HudConfig() {}

    public static void toggle() {
        enabled = !enabled;
    }
}
