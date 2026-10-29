package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Saves/loads HUD toggles, waypoints, death marker, and measure points
 * to config/hudstuff.properties so state survives restarts.
 */
public final class ConfigIO {
    private static final String FILE_NAME = "hudstuff.properties";

    private ConfigIO() {}

    private static Path path() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(FILE_NAME);
    }

    public static void load() {
        Path p = path();
        if (!Files.isRegularFile(p)) return;
        try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(r);

            HudConfig.enabled = bool(props, "enabled", HudConfig.enabled);
            HudConfig.showCoords = bool(props, "showCoords", HudConfig.showCoords);
            HudConfig.showFacing = bool(props, "showFacing", HudConfig.showFacing);
            HudConfig.showBiome = bool(props, "showBiome", HudConfig.showBiome);
            HudConfig.showLight = bool(props, "showLight", HudConfig.showLight);
            HudConfig.showChunk = bool(props, "showChunk", HudConfig.showChunk);
            HudConfig.showTime = bool(props, "showTime", HudConfig.showTime);
            HudConfig.showFps = bool(props, "showFps", HudConfig.showFps);
            HudConfig.showSpeed = bool(props, "showSpeed", HudConfig.showSpeed);
            HudConfig.showMoon = bool(props, "showMoon", HudConfig.showMoon);
            HudConfig.showArmor = bool(props, "showArmor", HudConfig.showArmor);
            HudConfig.showXp = bool(props, "showXp", HudConfig.showXp);
            HudConfig.showHeldItem = bool(props, "showHeldItem", HudConfig.showHeldItem);
            HudConfig.showArrowCount = bool(props, "showArrowCount", HudConfig.showArrowCount);
            HudConfig.inventoryFullWarn = bool(props, "inventoryFullWarn", HudConfig.inventoryFullWarn);
            HudConfig.showDeath = bool(props, "showDeath", HudConfig.showDeath);
            HudConfig.showSlime = bool(props, "showSlime", HudConfig.showSlime);
            HudConfig.showLookAt = bool(props, "showLookAt", HudConfig.showLookAt);
            HudConfig.showCrop = bool(props, "showCrop", HudConfig.showCrop);
            HudConfig.showRedstone = bool(props, "showRedstone", HudConfig.showRedstone);
            HudConfig.showBlockCount = bool(props, "showBlockCount", HudConfig.showBlockCount);
            HudConfig.showEntity = bool(props, "showEntity", HudConfig.showEntity);
            HudConfig.showHoney = bool(props, "showHoney", HudConfig.showHoney);
            HudConfig.showMeasure = bool(props, "showMeasure", HudConfig.showMeasure);
            HudConfig.showWaypoints = bool(props, "showWaypoints", HudConfig.showWaypoints);
            HudConfig.chunkBorders = bool(props, "chunkBorders", HudConfig.chunkBorders);

            BlockCounter.radius = intVal(props, "countRadius", BlockCounter.radius);

            if (bool(props, "hasDeath", false)) {
                HudConfig.setDeath(
                    dbl(props, "deathX", 0),
                    dbl(props, "deathY", 0),
                    dbl(props, "deathZ", 0),
                    props.getProperty("deathDim", "")
                );
            }

            Waypoints.clear();
            int n = intVal(props, "wpCount", 0);
            for (int i = 0; i < n && i < Waypoints.MAX; i++) {
                String name = props.getProperty("wp." + i + ".name", "WP" + (i + 1));
                double x = dbl(props, "wp." + i + ".x", 0);
                double y = dbl(props, "wp." + i + ".y", 0);
                double z = dbl(props, "wp." + i + ".z", 0);
                String dim = props.getProperty("wp." + i + ".dim", "");
                Waypoints.add(name, x, y, z, dim);
            }

            if (HudConfig.chunkBorders) {
                ChunkBorders.setEnabled(true);
            }

            HudStuff.LOGGER.info("Loaded HUD config ({} waypoints)", Waypoints.size());
        } catch (Exception e) {
            HudStuff.LOGGER.warn("Failed to load HUD config: {}", e.toString());
        }
    }

    public static void save() {
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            Properties props = new Properties();

            props.setProperty("enabled", String.valueOf(HudConfig.enabled));
            props.setProperty("showCoords", String.valueOf(HudConfig.showCoords));
            props.setProperty("showFacing", String.valueOf(HudConfig.showFacing));
            props.setProperty("showBiome", String.valueOf(HudConfig.showBiome));
            props.setProperty("showLight", String.valueOf(HudConfig.showLight));
            props.setProperty("showChunk", String.valueOf(HudConfig.showChunk));
            props.setProperty("showTime", String.valueOf(HudConfig.showTime));
            props.setProperty("showFps", String.valueOf(HudConfig.showFps));
            props.setProperty("showSpeed", String.valueOf(HudConfig.showSpeed));
            props.setProperty("showMoon", String.valueOf(HudConfig.showMoon));
            props.setProperty("showArmor", String.valueOf(HudConfig.showArmor));
            props.setProperty("showXp", String.valueOf(HudConfig.showXp));
            props.setProperty("showHeldItem", String.valueOf(HudConfig.showHeldItem));
            props.setProperty("showArrowCount", String.valueOf(HudConfig.showArrowCount));
            props.setProperty("inventoryFullWarn", String.valueOf(HudConfig.inventoryFullWarn));
            props.setProperty("showDeath", String.valueOf(HudConfig.showDeath));
            props.setProperty("showSlime", String.valueOf(HudConfig.showSlime));
            props.setProperty("showLookAt", String.valueOf(HudConfig.showLookAt));
            props.setProperty("showCrop", String.valueOf(HudConfig.showCrop));
            props.setProperty("showRedstone", String.valueOf(HudConfig.showRedstone));
            props.setProperty("showBlockCount", String.valueOf(HudConfig.showBlockCount));
            props.setProperty("showEntity", String.valueOf(HudConfig.showEntity));
            props.setProperty("showHoney", String.valueOf(HudConfig.showHoney));
            props.setProperty("showMeasure", String.valueOf(HudConfig.showMeasure));
            props.setProperty("showWaypoints", String.valueOf(HudConfig.showWaypoints));
            props.setProperty("chunkBorders", String.valueOf(ChunkBorders.isEffectivelyOn()));
            props.setProperty("countRadius", String.valueOf(BlockCounter.radius));

            props.setProperty("hasDeath", String.valueOf(HudConfig.hasDeath));
            if (HudConfig.hasDeath) {
                props.setProperty("deathX", String.valueOf(HudConfig.deathX));
                props.setProperty("deathY", String.valueOf(HudConfig.deathY));
                props.setProperty("deathZ", String.valueOf(HudConfig.deathZ));
                props.setProperty("deathDim", HudConfig.deathDim);
            }

            props.setProperty("wpCount", String.valueOf(Waypoints.size()));
            int i = 0;
            for (Waypoints.Entry e : Waypoints.all()) {
                props.setProperty("wp." + i + ".name", e.name);
                props.setProperty("wp." + i + ".x", String.valueOf(e.x));
                props.setProperty("wp." + i + ".y", String.valueOf(e.y));
                props.setProperty("wp." + i + ".z", String.valueOf(e.z));
                props.setProperty("wp." + i + ".dim", e.dim);
                i++;
            }

            try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
                props.store(w, "HUD Stuff config — auto-saved");
            }
            HudStuff.LOGGER.info("Saved HUD config");
        } catch (Exception e) {
            HudStuff.LOGGER.warn("Failed to save HUD config: {}", e.toString());
        }
    }

    private static boolean bool(Properties p, String k, boolean def) {
        String v = p.getProperty(k);
        if (v == null) return def;
        return Boolean.parseBoolean(v);
    }

    private static int intVal(Properties p, String k, int def) {
        try {
            return Integer.parseInt(p.getProperty(k, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static double dbl(Properties p, String k, double def) {
        try {
            return Double.parseDouble(p.getProperty(k, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
