package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/** Info about the block under the crosshair. */
public final class LookAtInfo {
    private LookAtInfo() {}

    public static String line(Minecraft mc, LocalPlayer player) {
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        BlockHitResult hit = (BlockHitResult) mc.hitResult;
        BlockPos pos = hit.getBlockPos();
        if (mc.level == null) return null;
        BlockState state = mc.level.getBlockState(pos);
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        float hardness = state.getDestroySpeed(mc.level, pos);
        int light = Math.max(
            mc.level.getBrightness(LightLayer.BLOCK, pos),
            mc.level.getBrightness(LightLayer.SKY, pos)
        );
        double dist = Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        return String.format(Locale.ROOT, "Looking: %s  h=%.1f  light=%d  %.1fm",
            id, hardness, light, dist);
    }
}
