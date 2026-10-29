package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudOverlay {
    private HudOverlay() {}

    public static void render(GuiGraphicsExtractor graphics) {
        if (!HudConfig.enabled) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.getDebugOverlay().showDebugScreen()) return;

        Font font = mc.font;
        List<String> lines = buildLines(mc, player);

        int x = 4;
        int y = 4;
        int lineHeight = font.lineHeight + 1;

        for (String line : lines) {
            graphics.text(font, line, x, y, HudConfig.color, true);
            y += lineHeight;
        }
    }

    private static List<String> buildLines(Minecraft mc, LocalPlayer player) {
        List<String> lines = new ArrayList<>(10);
        BlockPos pos = player.blockPosition();

        if (HudConfig.showCoords) {
            lines.add(String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                player.getX(), player.getY(), player.getZ()));
            lines.add(String.format(Locale.ROOT, "Block: %d %d %d",
                pos.getX(), pos.getY(), pos.getZ()));
        }

        if (HudConfig.showFacing) {
            Direction facing = player.getDirection();
            float yaw = player.getYRot();
            lines.add(String.format(Locale.ROOT, "Facing: %s (%.1f)",
                facing.getSerializedName(), yaw));
        }

        if (HudConfig.showChunk) {
            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            lines.add(String.format(Locale.ROOT, "Chunk: %d %d  (in: %d %d)",
                cx, cz, pos.getX() & 15, pos.getZ() & 15));
        }

        if (HudConfig.showBiome) {
            Holder<Biome> biome = mc.level.getBiome(pos);
            String biomeId = "unknown";
            var key = biome.unwrapKey();
            if (key.isPresent()) {
                biomeId = key.get().identifier().toString();
            }
            lines.add("Biome: " + biomeId);
        }

        if (HudConfig.showLight) {
            int blockLight = mc.level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = mc.level.getBrightness(LightLayer.SKY, pos);
            lines.add(String.format(Locale.ROOT, "Light: %d  (block %d / sky %d)",
                Math.max(blockLight, skyLight), blockLight, skyLight));
        }

        if (HudConfig.showTime) {
            long gameTime = mc.level.getGameTime();
            long dayTime = gameTime % 24000L;
            long day = gameTime / 24000L;
            int hours = (int) ((dayTime / 1000L + 6) % 24);
            int minutes = (int) ((dayTime % 1000L) * 60 / 1000L);
            lines.add(String.format(Locale.ROOT, "Day %d  %02d:%02d", day, hours, minutes));
        }

        if (HudConfig.showDeath && HudConfig.hasDeath) {
            String currentDim = mc.level.dimension().identifier().toString();
            if (currentDim.equals(HudConfig.deathDim)) {
                double dx = HudConfig.deathX - player.getX();
                double dy = HudConfig.deathY - player.getY();
                double dz = HudConfig.deathZ - player.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                lines.add(String.format(Locale.ROOT, "Death: %.0f %.0f %.0f  (%.0fm)",
                    HudConfig.deathX, HudConfig.deathY, HudConfig.deathZ, dist));
            } else {
                lines.add(String.format(Locale.ROOT, "Death: %.0f %.0f %.0f [%s]",
                    HudConfig.deathX, HudConfig.deathY, HudConfig.deathZ, HudConfig.deathDim));
            }
        }

        lines.add("FPS: " + mc.getFps());
        return lines;
    }
}
