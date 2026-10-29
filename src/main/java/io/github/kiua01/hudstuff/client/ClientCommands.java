package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client chat commands (intercepted, not sent to server):
 *   .hud | .chunk | .wp set | .wp clear | .death clear | .hud help
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

            if (msg.equalsIgnoreCase(".hud") || msg.equalsIgnoreCase(".hud toggle")) {
                HudConfig.toggle();
                HudStuff.LOGGER.info("HUD {}", HudConfig.enabled ? "on" : "off");
                return false;
            }
            if (msg.equalsIgnoreCase(".chunk") || msg.equalsIgnoreCase(".chunkborders")
                    || msg.equalsIgnoreCase(".borders")) {
                ChunkBorders.toggle();
                HudStuff.LOGGER.info("Chunk borders {}", ChunkBorders.isEffectivelyOn() ? "ON" : "OFF");
                return false;
            }
            if (msg.equalsIgnoreCase(".wp set") || msg.equalsIgnoreCase(".waypoint set")) {
                String dim = mc.level == null ? "" : mc.level.dimension().identifier().toString();
                HudConfig.setWaypoint(player.getX(), player.getY(), player.getZ(), dim, "Waypoint");
                HudStuff.LOGGER.info("Waypoint set at {}, {}, {}", player.getX(), player.getY(), player.getZ());
                return false;
            }
            if (msg.equalsIgnoreCase(".wp clear") || msg.equalsIgnoreCase(".waypoint clear")) {
                HudConfig.clearWaypoint();
                HudStuff.LOGGER.info("Waypoint cleared");
                return false;
            }
            if (msg.equalsIgnoreCase(".death clear")) {
                HudConfig.clearDeath();
                HudStuff.LOGGER.info("Death marker cleared");
                return false;
            }
            if (msg.equalsIgnoreCase(".hud help")) {
                HudStuff.LOGGER.info(".hud | .chunk | .wp set | .wp clear | .death clear");
                return false;
            }
            return true;
        });
    }
}
