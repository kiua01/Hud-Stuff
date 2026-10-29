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
    private static Object debugRendererCache;
    private static Field borderFlagField;
    private static Method borderToggleMethod;
    private static boolean resolved;

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

    private static void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null) return;
            // Find debug renderer field on Minecraft
            for (Field f : Minecraft.class.getDeclaredFields()) {
                String n = f.getName().toLowerCase();
                if (n.contains("debug") && n.contains("render")) {
                    f.setAccessible(true);
                    debugRendererCache = f.get(mc);
                    if (debugRendererCache != null) break;
                }
            }
            if (debugRendererCache == null) {
                // try exact known names
                for (String name : new String[]{"debugRenderer", "debugRenderers", "gameRenderer"}) {
                    try {
                        Field f = Minecraft.class.getDeclaredField(name);
                        f.setAccessible(true);
                        Object v = f.get(mc);
                        if (v != null && v.getClass().getName().toLowerCase().contains("debug")) {
                            debugRendererCache = v;
                            break;
                        }
                    } catch (NoSuchFieldException ignored) {}
                }
            }
            if (debugRendererCache == null) return;

            Class<?> cls = debugRendererCache.getClass();
            for (Field f : cls.getDeclaredFields()) {
                String n = f.getName().toLowerCase();
                if ((n.contains("chunk") && n.contains("border")) || n.equals("renderchunkborder")) {
                    if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                        f.setAccessible(true);
                        borderFlagField = f;
                        break;
                    }
                }
            }
            for (Method m : cls.getDeclaredMethods()) {
                String n = m.getName().toLowerCase();
                if (n.contains("chunk") && n.contains("border") && m.getParameterCount() == 0) {
                    m.setAccessible(true);
                    borderToggleMethod = m;
                    break;
                }
            }
        } catch (Throwable t) {
            HudStuff.LOGGER.debug("ChunkBorders resolve failed: {}", t.toString());
        }
    }

    private static void apply() {
        if (forcedOn == null) return;
        resolve();
        try {
            if (borderFlagField != null && debugRendererCache != null) {
                borderFlagField.set(debugRendererCache, forcedOn);
                return;
            }
            if (borderToggleMethod != null && debugRendererCache != null) {
                if (readVanillaFlag() != forcedOn) {
                    borderToggleMethod.invoke(debugRendererCache);
                }
            }
        } catch (Throwable t) {
            HudStuff.LOGGER.debug("ChunkBorders apply failed: {}", t.toString());
        }
    }

    private static boolean readVanillaFlag() {
        resolve();
        try {
            if (borderFlagField != null && debugRendererCache != null) {
                Object v = borderFlagField.get(debugRendererCache);
                if (v instanceof Boolean b) return b;
            }
        } catch (Throwable ignored) {}
        return false;
    }
}
