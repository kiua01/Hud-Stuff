package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Crosshair target info — general block line + crop growth + redstone power
 * (ports Bedrock crops.js / redstone.js action-bar lines).
 */
public final class LookAtInfo {
    private LookAtInfo() {}

    public static List<String> lines(Minecraft mc, LocalPlayer player) {
        List<String> out = new ArrayList<>(3);
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return out;
        }
        BlockHitResult hit = (BlockHitResult) mc.hitResult;
        BlockPos pos = hit.getBlockPos();
        if (mc.level == null) return out;
        BlockState state = mc.level.getBlockState(pos);
        Block block = state.getBlock();
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();

        float hardness = state.getDestroySpeed(mc.level, pos);
        int light = Math.max(
            mc.level.getBrightness(LightLayer.BLOCK, pos),
            mc.level.getBrightness(LightLayer.SKY, pos)
        );
        double dist = Math.sqrt(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));

        if (HudConfig.showLookAt) {
            out.add(String.format(Locale.ROOT, "Looking: %s  h=%.1f  light=%d  %.1fm",
                id, hardness, light, dist));
        }

        if (HudConfig.showCrop) {
            String crop = cropLine(state, block);
            if (crop != null) out.add(crop);
        }

        if (HudConfig.showRedstone) {
            String rs = redstoneLine(mc, pos, state, id);
            if (rs != null) out.add(rs);
        }

        return out;
    }

    private static String cropLine(BlockState state, Block block) {
        // Standard crops (wheat, carrots, potatoes, beetroot)
        if (block instanceof CropBlock crop) {
            int age = crop.getAge(state);
            int max = crop.getMaxAge();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return formatGrowth(name, age, max);
        }
        if (block instanceof NetherWartBlock) {
            int age = state.getValue(NetherWartBlock.AGE);
            return formatGrowth("nether_wart", age, 3);
        }
        if (block instanceof CocoaBlock) {
            int age = state.getValue(CocoaBlock.AGE);
            return formatGrowth("cocoa", age, 2);
        }
        if (block instanceof SweetBerryBushBlock) {
            int age = state.getValue(SweetBerryBushBlock.AGE);
            return formatGrowth("sweet_berries", age, 3);
        }
        if (block instanceof StemBlock) {
            int age = state.getValue(StemBlock.AGE);
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return formatGrowth(name, age, 7);
        }
        // Pitcher / torchflower crop via generic age property
        Optional<IntegerProperty> ageProp = findAgeProperty(state);
        if (ageProp.isPresent()) {
            IntegerProperty prop = ageProp.get();
            int age = state.getValue(prop);
            int max = prop.getPossibleValues().stream().mapToInt(i -> i).max().orElse(age);
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            if (name.contains("crop") || name.contains("sapling") || name.contains("torchflower")
                    || name.contains("pitcher")) {
                return formatGrowth(name, age, max);
            }
        }
        // Farmland moisture
        if (block == Blocks.FARMLAND) {
            int moisture = state.getValue(BlockStateProperties.MOISTURE);
            return String.format(Locale.ROOT, "Farmland moisture: %d/7%s",
                moisture, moisture == 7 ? " (wet)" : "");
        }
        return null;
    }

    private static String formatGrowth(String name, int age, int max) {
        boolean ready = age >= max;
        return String.format(Locale.ROOT, "Crop: %s  %d/%d%s",
            name, age, max, ready ? "  READY" : "");
    }

    private static Optional<IntegerProperty> findAgeProperty(BlockState state) {
        for (Property<?> p : state.getProperties()) {
            if (p instanceof IntegerProperty ip && p.getName().equals("age")) {
                return Optional.of(ip);
            }
        }
        return Optional.empty();
    }

    private static String redstoneLine(Minecraft mc, BlockPos pos, BlockState state, String id) {
        boolean interesting =
            id.contains("redstone")
                || id.contains("repeater")
                || id.contains("comparator")
                || id.contains("observer")
                || id.contains("piston")
                || id.contains("dispenser")
                || id.contains("dropper")
                || id.contains("hopper")
                || id.contains("lever")
                || id.contains("button")
                || id.contains("pressure_plate")
                || id.contains("daylight_detector")
                || id.contains("sculk_sensor")
                || id.contains("target")
                || id.contains("copper_bulb")
                || id.contains("crafter")
                || id.contains("lamp");

        if (!interesting) {
            // Still show signal strength if the block can output power
            int signal = mc.level.getSignal(pos, net.minecraft.core.Direction.UP);
            int direct = 0;
            for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                direct = Math.max(direct, mc.level.getSignal(pos, d));
            }
            if (direct <= 0 && signal <= 0) return null;
            return String.format(Locale.ROOT, "Redstone signal: %d", Math.max(signal, direct));
        }

        int best = 0;
        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
            best = Math.max(best, mc.level.getSignal(pos, d));
            best = Math.max(best, mc.level.getDirectSignal(pos, d));
        }
        // Wire power property
        if (state.hasProperty(BlockStateProperties.POWER)) {
            best = Math.max(best, state.getValue(BlockStateProperties.POWER));
        }
        boolean powered = false;
        if (state.hasProperty(BlockStateProperties.POWERED)) {
            powered = state.getValue(BlockStateProperties.POWERED);
        }
        if (state.hasProperty(BlockStateProperties.LIT)) {
            powered = powered || state.getValue(BlockStateProperties.LIT);
        }

        StringBuilder sb = new StringBuilder("Redstone: ");
        sb.append("power=").append(best);
        if (state.hasProperty(BlockStateProperties.POWERED) || state.hasProperty(BlockStateProperties.LIT)) {
            sb.append(powered ? " ON" : " OFF");
        }
        if (state.hasProperty(BlockStateProperties.DELAY)) {
            sb.append(" delay=").append(state.getValue(BlockStateProperties.DELAY));
        }
        if (state.hasProperty(BlockStateProperties.MODE_COMPARATOR)) {
            sb.append(" mode=").append(state.getValue(BlockStateProperties.MODE_COMPARATOR).getSerializedName());
        }
        return sb.toString();
    }
}
