package io.github.kiua01.hudstuff.client;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

/**
 * Lightweight client chat commands (intercepted, not sent to server):
 *   .hud          — toggle HUD
 *   .wp set       — set waypoint at feet
 *   .wp clear     — clear waypoint
 *   .death clear  — clear death marker
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
                tip(player, HudConfig.enabled ? "HUD on" : "HUD off");
                return false;
            }
            if (msg.equalsIgnoreCase(".wp set") || msg.equalsIgnoreCase(".waypoint set")) {
                String dim = mc.level == null ? "" : mc.level.dimension().identifier().toString();
                HudConfig.setWaypoint(player.getX(), player.getY(), player.getZ(), dim, "Waypoint");
                tip(player, String.format("Waypoint set at %.0f %.0f %.0f", player.getX(), player.getY(), player.getZ()));
                return false;
            }
            if (msg.equalsIgnoreCase(".wp clear") || msg.equalsIgnoreCase(".waypoint clear")) {
                HudConfig.clearWaypoint();
                tip(player, "Waypoint cleared");
                return false;
            }
            if (msg.equalsIgnoreCase(".death clear")) {
                HudConfig.clearDeath();
                tip(player, "Death marker cleared");
                return false;
            }
            if (msg.equalsIgnoreCase(".hud help")) {
                tip(player, ".hud | .wp set | .wp clear | .death clear");
                return false;
            }
            return true;
        });
    }

    private static void tip(LocalPlayer player, String text) {
        player.displayClientMessage(Component.literal("[HUD] " + text), true);
    }
}
