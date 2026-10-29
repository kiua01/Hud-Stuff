package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client chat commands (not sent to server).
 *
 *   .hud / .hud help
 *   .chunk
 *   .wp set [name] | .wp list | .wp del <n> | .wp clear
 *   .measure a | .measure b | .measure clear
 *   .death clear
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
                HudStuff.LOGGER.info("HUD {}", HudConfig.enabled ? "on" : "off");
                return false;
            }
            if (lower.equals(".chunk") || lower.equals(".chunkborders") || lower.equals(".borders")) {
                ChunkBorders.toggle();
                HudStuff.LOGGER.info("Chunk borders {}", ChunkBorders.isEffectivelyOn() ? "ON" : "OFF");
                return false;
            }
            if (lower.equals(".death clear")) {
                HudConfig.clearDeath();
                HudStuff.LOGGER.info("Death marker cleared");
                return false;
            }

            // Waypoints
            if (lower.equals(".wp set") || lower.startsWith(".wp set ")) {
                String name = "WP" + (Waypoints.size() + 1);
                if (msg.length() > 8) name = msg.substring(8).trim();
                if (name.isEmpty()) name = "WP" + (Waypoints.size() + 1);
                if (Waypoints.add(name, player.getX(), player.getY(), player.getZ(), dim)) {
                    HudStuff.LOGGER.info("Waypoint {} saved ({}/{})", name, Waypoints.size(), Waypoints.MAX);
                } else {
                    HudStuff.LOGGER.info("Waypoint list full (max {})", Waypoints.MAX);
                }
                return false;
            }
            if (lower.equals(".wp list")) {
                if (Waypoints.size() == 0) {
                    HudStuff.LOGGER.info("No waypoints");
                } else {
                    int i = 1;
                    for (Waypoints.Entry e : Waypoints.all()) {
                        HudStuff.LOGGER.info("{}. {}", i++, e);
                    }
                }
                return false;
            }
            if (lower.startsWith(".wp del ")) {
                try {
                    int n = Integer.parseInt(msg.substring(8).trim());
                    if (Waypoints.remove(n)) HudStuff.LOGGER.info("Removed WP{}", n);
                    else HudStuff.LOGGER.info("No WP{}", n);
                } catch (NumberFormatException e) {
                    HudStuff.LOGGER.info("Usage: .wp del <number>");
                }
                return false;
            }
            if (lower.equals(".wp clear")) {
                Waypoints.clear();
                HudConfig.clearWaypoint();
                HudStuff.LOGGER.info("All waypoints cleared");
                return false;
            }

            // Distance measure
            if (lower.equals(".measure a") || lower.equals(".measurea")) {
                DistanceMeasure.setA(player.getX(), player.getY(), player.getZ());
                HudStuff.LOGGER.info("Measure point A set");
                return false;
            }
            if (lower.equals(".measure b") || lower.equals(".measureb")) {
                DistanceMeasure.setB(player.getX(), player.getY(), player.getZ());
                HudStuff.LOGGER.info("Measure point B set");
                return false;
            }
            if (lower.equals(".measure clear") || lower.equals(".measure")) {
                if (lower.equals(".measure")) {
                    HudStuff.LOGGER.info(".measure a | .measure b | .measure clear");
                } else {
                    DistanceMeasure.clear();
                    HudStuff.LOGGER.info("Measure cleared");
                }
                return false;
            }

            if (lower.equals(".hud help")) {
                HudStuff.LOGGER.info(".hud | .chunk | .wp set/list/del/clear | .measure a/b/clear | .death clear");
                return false;
            }
            return true;
        });
    }
}
