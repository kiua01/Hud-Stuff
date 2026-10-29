package io.github.kiua01.hudstuff.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;

/** Horizontal speed in blocks/second (20 ticks). */
public final class SpeedTracker {
    private static double lastX, lastZ;
    private static boolean hasLast;
    private static double speedBps;

    private SpeedTracker() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer p = client.player;
            if (p == null) {
                hasLast = false;
                speedBps = 0;
                return;
            }
            double x = p.getX();
            double z = p.getZ();
            if (hasLast) {
                double dx = x - lastX;
                double dz = z - lastZ;
                speedBps = Math.sqrt(dx * dx + dz * dz) * 20.0;
            }
            lastX = x;
            lastZ = z;
            hasLast = true;
        });
    }

    public static double speedBps() {
        return speedBps;
    }
}
