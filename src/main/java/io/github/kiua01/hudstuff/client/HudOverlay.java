package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Draws the main HUD lines on the client (26.3). */
public final class HudOverlay {
    private HudOverlay() {}

    public static void render(GuiGraphicsExtractor graphics, float tickDelta) {
        if (!HudConfig.enabled) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.options.hideGui) return;
        if (mc.getDebugOverlay().showDebugScreen()) return;

        Font font = mc.font;
        List<String> lines = buildLines(mc, player);

        int x = 4;
        int y = 4;
        int lineHeight = font.lineHeight + 1;

        for (String line : lines) {
            graphics.drawString(font, line, x, y, HudConfig.color, true);
            y += lineHeight;
        }
    }

    private static List<String> buildLines(Minecraft mc, LocalPlayer player) {
        List<String> lines = new ArrayList<>(8);
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
            int lx = pos.getX() & 15;
            int lz = pos.getZ() & 15;
            lines.add(String.format(Locale.ROOT, "Chunk: %d %d  (in: %d %d)", cx, cz, lx, lz));
        }

        if (HudConfig.showBiome) {
            Holder<Biome> biome = mc.level.getBiome(pos);
            String biomeId = biome.unwrapKey()
                .map(ResourceKey::location)
                .map(Object::toString)
                .orElse("unknown");
            lines.add("Biome: " + biomeId);
        }

        if (HudConfig.showLight) {
            int blockLight = mc.level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = mc.level.getBrightness(LightLayer.SKY, pos);
            lines.add(String.format(Locale.ROOT, "Light: %d  (block %d / sky %d)",
                Math.max(blockLight, skyLight), blockLight, skyLight));
        }

        if (HudConfig.showTime) {
            long dayTime = mc.level.getDayTime() % 24000L;
            long day = mc.level.getDayTime() / 24000L;
            int hours = (int) ((dayTime / 1000L + 6) % 24);
            int minutes = (int) ((dayTime % 1000L) * 60 / 1000L);
            lines.add(String.format(Locale.ROOT, "Day %d  %02d:%02d", day, hours, minutes));
        }

        lines.add("FPS: " + mc.getFps());
        return lines;
    }
}
