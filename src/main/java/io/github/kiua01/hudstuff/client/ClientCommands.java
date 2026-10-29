package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client chat commands (not sent to server).
 */
public final class ClientCommands {
    private ClientCommands() {}

    public static void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            String msg = message.trim();
            if (!msg.startsWith(".")) return true;

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return true;
            String dim = mc.level == null ? "" : mc.level.dimension().identifier().toString();
            String lower = msg.toLowerCase();

            if (lower.equals(".hud") || lower.equals(".hud toggle")) {
                HudConfig.toggle();
                log("HUD " + (HudConfig.enabled ? "on" : "off"));
                return false;
            }
            // Per-feature toggles
            if (lower.equals(".hud crop")) { HudConfig.showCrop = !HudConfig.showCrop; log("crop " + on(HudConfig.showCrop)); return false; }
            if (lower.equals(".hud redstone") || lower.equals(".hud rs")) { HudConfig.showRedstone = !HudConfig.showRedstone; log("redstone " + on(HudConfig.showRedstone)); return false; }
            if (lower.equals(".hud look")) { HudConfig.showLookAt = !HudConfig.showLookAt; log("look-at " + on(HudConfig.showLookAt)); return false; }
            if (lower.equals(".hud armor")) { HudConfig.showArmor = !HudConfig.showArmor; log("armor " + on(HudConfig.showArmor)); return false; }
            if (lower.equals(".hud coords")) { HudConfig.showCoords = !HudConfig.showCoords; log("coords " + on(HudConfig.showCoords)); return false; }
            if (lower.equals(".hud biome")) { HudConfig.showBiome = !HudConfig.showBiome; log("biome " + on(HudConfig.showBiome)); return false; }
            if (lower.equals(".hud slime")) { HudConfig.showSlime = !HudConfig.showSlime; log("slime " + on(HudConfig.showSlime)); return false; }
            if (lower.equals(".hud fps")) { HudConfig.showFps = !HudConfig.showFps; log("fps " + on(HudConfig.showFps)); return false; }

            if (lower.equals(".chunk") || lower.equals(".chunkborders") || lower.equals(".borders")) {
                ChunkBorders.toggle();
                log("Chunk borders " + (ChunkBorders.isEffectivelyOn() ? "ON" : "OFF"));
                return false;
            }
            if (lower.equals(".death clear")) {
                HudConfig.clearDeath();
                log("Death marker cleared");
                return false;
            }

            if (lower.equals(".wp set") || lower.startsWith(".wp set ")) {
                String name = "WP" + (Waypoints.size() + 1);
                if (msg.length() > 8) name = msg.substring(8).trim();
                if (name.isEmpty()) name = "WP" + (Waypoints.size() + 1);
                if (Waypoints.add(name, player.getX(), player.getY(), player.getZ(), dim)) {
                    log("Waypoint " + name + " saved (" + Waypoints.size() + "/" + Waypoints.MAX + ")");
                } else {
                    log("Waypoint list full (max " + Waypoints.MAX + ")");
                }
                return false;
            }
            if (lower.equals(".wp list")) {
                if (Waypoints.size() == 0) log("No waypoints");
                else {
                    int i = 1;
                    for (Waypoints.Entry e : Waypoints.all()) log(i++ + ". " + e);
                }
                return false;
            }
            if (lower.startsWith(".wp del ")) {
                try {
                    int n = Integer.parseInt(msg.substring(8).trim());
                    log(Waypoints.remove(n) ? "Removed WP" + n : "No WP" + n);
                } catch (NumberFormatException e) {
                    log("Usage: .wp del <number>");
                }
                return false;
            }
            if (lower.equals(".wp clear")) {
                Waypoints.clear();
                HudConfig.clearWaypoint();
                log("All waypoints cleared");
                return false;
            }

            if (lower.equals(".measure a") || lower.equals(".measurea")) {
                DistanceMeasure.setA(player.getX(), player.getY(), player.getZ());
                log("Measure point A set");
                return false;
            }
            if (lower.equals(".measure b") || lower.equals(".measureb")) {
                DistanceMeasure.setB(player.getX(), player.getY(), player.getZ());
                log("Measure point B set");
                return false;
            }
            if (lower.equals(".measure clear")) {
                DistanceMeasure.clear();
                log("Measure cleared");
                return false;
            }
            if (lower.equals(".measure")) {
                log(".measure a | .measure b | .measure clear");
                return false;
            }

            if (lower.equals(".hud help")) {
                log(".hud [crop|redstone|look|armor|coords|biome|slime|fps]");
                log(".chunk | .wp set/list/del/clear | .measure a/b/clear | .death clear");
                return false;
            }
            return true;
        });
    }

    private static String on(boolean b) { return b ? "ON" : "OFF"; }
    private static void log(String s) { HudStuff.LOGGER.info(s); }
}
