package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Chunk borders matching original HUD Essentials intent:
 * persistent world-space border around the player's current chunk.
 * Drives the same built-in renderer as F3+G (multicolor wireframe).
 */
public final class ChunkBorders {
    private static Boolean forcedOn = null;

    private ChunkBorders() {}

    public static void setEnabled(boolean enabled) {
        forcedOn = enabled;
        HudConfig.chunkBorders = enabled;
        apply();
    }

    public static void toggle() {
        setEnabled(!isEffectivelyOn());
    }

    public static boolean isEffectivelyOn() {
        if (forcedOn != null) return forcedOn;
        return readVanillaFlag();
    }

    public static void tick() {
        if (forcedOn != null) apply();
    }

    private static void apply() {
        if (forcedOn == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        try {
            Object debugRenderer = mc.debugRenderer;
            if (debugRenderer == null) return;

            for (String name : new String[]{"renderChunkborder", "chunkBorderRenderer", "renderChunkBorder"}) {
                try {
                    Field f = debugRenderer.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                        f.set(debugRenderer, forcedOn);
                        return;
                    }
                } catch (NoSuchFieldException ignored) {}
            }

            for (String name : new String[]{"toggleRenderChunkBorder", "switchRenderChunkborder", "toggleChunkBorders"}) {
                try {
                    Method m = debugRenderer.getClass().getDeclaredMethod(name);
                    m.setAccessible(true);
                    if (readVanillaFlag() != forcedOn) m.invoke(debugRenderer);
                    return;
                } catch (NoSuchMethodException ignored) {}
            }
        } catch (Throwable t) {
            HudStuff.LOGGER.debug("Chunk border toggle failed: {}", t.toString());
        }
    }

    private static boolean readVanillaFlag() {
        try {
            Object debugRenderer = Minecraft.getInstance().debugRenderer;
            if (debugRenderer == null) return false;
            for (String name : new String[]{"renderChunkborder", "chunkBorderRenderer", "renderChunkBorder"}) {
                try {
                    Field f = debugRenderer.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object v = f.get(debugRenderer);
                    if (v instanceof Boolean b) return b;
                } catch (NoSuchFieldException ignored) {}
            }
        } catch (Throwable ignored) {}
        return false;
    }
}
