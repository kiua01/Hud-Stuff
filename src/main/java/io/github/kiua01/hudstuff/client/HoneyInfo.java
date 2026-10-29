package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/** Beehive / bee nest honey level (ports Bedrock honey.js). */
public final class HoneyInfo {
    private HoneyInfo() {}

    public static String line(Minecraft mc) {
        if (!HudConfig.showHoney) return null;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return null;
        if (mc.level == null) return null;

        BlockPos pos = ((BlockHitResult) mc.hitResult).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!(state.getBlock() instanceof BeehiveBlock)) return null;

        int honey = state.getValue(BeehiveBlock.HONEY_LEVEL);
        int bees = 0;
        var be = mc.level.getBlockEntity(pos);
        if (be instanceof BeehiveBlockEntity hive) {
            bees = hive.getOccupantCount();
        }
        String name = state.is(Blocks.BEE_NEST) ? "Bee nest" : "Beehive";
        return String.format(Locale.ROOT, "%s: honey %d/5  bees %d%s",
            name, honey, bees, honey >= 5 ? "  FULL" : "");
    }
}
