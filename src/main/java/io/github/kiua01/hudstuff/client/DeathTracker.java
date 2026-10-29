package io.github.kiua01.hudstuff.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;

/** Tracks the local player's last death position on the client. */
public final class DeathTracker {
    private static boolean wasAlive = true;

    private DeathTracker() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if (player == null) {
                wasAlive = true;
                return;
            }

            boolean alive = player.isAlive() && player.getHealth() > 0;
            if (wasAlive && !alive) {
                String dim = "";
                if (client.level != null) {
                    dim = client.level.dimension().identifier().toString();
                }
                HudConfig.setDeath(player.getX(), player.getY(), player.getZ(), dim);
            }
            wasAlive = alive;
        });
    }
}
