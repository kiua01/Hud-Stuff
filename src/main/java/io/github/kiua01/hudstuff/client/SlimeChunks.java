package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

import java.util.Random;

/** Vanilla slime-chunk formula for the current player chunk. */
public final class SlimeChunks {
    private SlimeChunks() {}

    public static boolean isSlimeChunk(LocalPlayer player, Level level) {
        if (player == null || level == null) return false;
        long seed;
        try {
            var server = Minecraft.getInstance().getSingleplayerServer();
            if (server != null) {
                seed = server.overworld().getSeed();
            } else {
                return false;
            }
        } catch (Throwable t) {
            return false;
        }
        int cx = player.blockPosition().getX() >> 4;
        int cz = player.blockPosition().getZ() >> 4;
        Random rnd = new Random(
            seed
                + (long) cx * cx * 0x4c1906L
                + (long) cx * 0x5ac0dbL
                + (long) cz * cz * 0x4307a7L
                + (long) cz * 0x5f24fL
                ^ 0x3ad8025fL
        );
        return rnd.nextInt(10) == 0;
    }
}
