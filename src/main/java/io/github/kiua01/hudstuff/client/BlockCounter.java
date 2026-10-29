package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/**
 * Counts matching blocks in a cube around the look-at target
 * (ports Bedrock block_counter tool behavior at a smaller, free radius).
 */
public final class BlockCounter {
    public static int radius = 8; // half-extent of cube

    private BlockCounter() {}

    public static String line(Minecraft mc) {
        if (!HudConfig.showBlockCount) return null;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return null;
        if (mc.level == null) return null;

        BlockHitResult hit = (BlockHitResult) mc.hitResult;
        BlockPos center = hit.getBlockPos();
        BlockState target = mc.level.getBlockState(center);
        Block block = target.getBlock();
        String id = BuiltInRegistries.BLOCK.getKey(block).getPath();

        int count = 0;
        int r = radius;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    m.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (mc.level.getBlockState(m).is(block)) count++;
                }
            }
        }
        int size = r * 2 + 1;
        return String.format(Locale.ROOT, "Count: %s x%d  (%dx%dx%d)", id, count, size, size, size);
    }
}
