package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Full HUD overlay — ports the action-bar / status lines from
 * Bedrock HUD Essentials (coords, light, biome, armor, XP, speed,
 * held item, arrows, inventory warn, death, waypoint, moon, FPS).
 */
public final class HudOverlay {
    private static final String[] MOON_PHASES = {
        "Full", "Waning Gibbous", "Last Quarter", "Waning Crescent",
        "New", "Waxing Crescent", "First Quarter", "Waxing Gibbous"
    };

    private HudOverlay() {}

    public static void render(GuiGraphicsExtractor graphics) {
        if (!HudConfig.enabled) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;
        if (mc.getDebugOverlay().showDebugScreen()) return;

        Font font = mc.font;
        List<Line> lines = buildLines(mc, player);

        int x = 4;
        int y = 4;
        int lineHeight = font.lineHeight + 1;

        for (Line line : lines) {
            graphics.text(font, line.text, x, y, line.color, true);
            y += lineHeight;
        }
    }

    private record Line(String text, int color) {}

    private static List<Line> buildLines(Minecraft mc, LocalPlayer player) {
        List<Line> lines = new ArrayList<>(16);
        BlockPos pos = player.blockPosition();
        int c = HudConfig.color;
        int w = HudConfig.warnColor;

        if (HudConfig.showCoords) {
            lines.add(new Line(String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f",
                player.getX(), player.getY(), player.getZ()), c));
            lines.add(new Line(String.format(Locale.ROOT, "Block: %d %d %d",
                pos.getX(), pos.getY(), pos.getZ()), c));
        }

        if (HudConfig.showFacing) {
            Direction facing = player.getDirection();
            lines.add(new Line(String.format(Locale.ROOT, "Facing: %s (%.1f)",
                facing.getSerializedName(), player.getYRot()), c));
        }

        if (HudConfig.showChunk) {
            lines.add(new Line(String.format(Locale.ROOT, "Chunk: %d %d  (in: %d %d)",
                pos.getX() >> 4, pos.getZ() >> 4, pos.getX() & 15, pos.getZ() & 15), c));
        }

        if (HudConfig.showBiome) {
            Holder<Biome> biome = mc.level.getBiome(pos);
            String biomeId = "unknown";
            var key = biome.unwrapKey();
            if (key.isPresent()) biomeId = key.get().identifier().toString();
            lines.add(new Line("Biome: " + biomeId, c));
        }

        if (HudConfig.showLight) {
            int blockLight = mc.level.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = mc.level.getBrightness(LightLayer.SKY, pos);
            lines.add(new Line(String.format(Locale.ROOT, "Light: %d  (block %d / sky %d)",
                Math.max(blockLight, skyLight), blockLight, skyLight), c));
        }

        if (HudConfig.showTime) {
            long gameTime = mc.level.getGameTime();
            long dayTime = gameTime % 24000L;
            long day = gameTime / 24000L;
            int hours = (int) ((dayTime / 1000L + 6) % 24);
            int minutes = (int) ((dayTime % 1000L) * 60 / 1000L);
            lines.add(new Line(String.format(Locale.ROOT, "Day %d  %02d:%02d", day, hours, minutes), c));
        }

        if (HudConfig.showMoon) {
            int phase = (int) ((mc.level.getGameTime() / 24000L) % 8);
            if (phase < 0) phase = 0;
            if (phase > 7) phase = 7;
            lines.add(new Line("Moon: " + MOON_PHASES[phase], c));
        }

        if (HudConfig.showSpeed) {
            lines.add(new Line(String.format(Locale.ROOT, "Speed: %.2f b/s", SpeedTracker.speedBps()), c));
        }

        if (HudConfig.showArmor) {
            lines.add(new Line(armorLine(player), c));
        }

        if (HudConfig.showXp) {
            int level = player.experienceLevel;
            float progress = player.experienceProgress * 100f;
            lines.add(new Line(String.format(Locale.ROOT, "XP: lvl %d  (%.0f%%)", level, progress), c));
        }

        if (HudConfig.showHeldItem) {
            ItemStack main = player.getMainHandItem();
            if (!main.isEmpty()) {
                String name = main.getHoverName().getString();
                if (main.isDamageableItem()) {
                    int max = main.getMaxDamage();
                    int dmg = main.getDamageValue();
                    int left = max - dmg;
                    int pct = max <= 0 ? 100 : (left * 100 / max);
                    int col = pct <= 15 ? w : c;
                    lines.add(new Line(String.format(Locale.ROOT, "Held: %s  %d/%d (%d%%)",
                        name, left, max, pct), col));
                } else {
                    lines.add(new Line("Held: " + name + " x" + main.getCount(), c));
                }
            }
        }

        if (HudConfig.showArrowCount) {
            int arrows = countItem(player, Items.ARROW)
                + countItem(player, Items.SPECTRAL_ARROW)
                + countItem(player, Items.TIPPED_ARROW);
            lines.add(new Line("Arrows: " + arrows, arrows == 0 ? w : c));
        }

        if (HudConfig.inventoryFullWarn) {
            int used = 0;
            int total = player.getInventory().getContainerSize();
            for (int i = 0; i < total; i++) {
                if (!player.getInventory().getItem(i).isEmpty()) used++;
            }
            // Main inventory slots are 0-35 (36 slots); hotbar included
            int mainSlots = 36;
            int usedMain = 0;
            for (int i = 0; i < mainSlots; i++) {
                if (!player.getInventory().getItem(i).isEmpty()) usedMain++;
            }
            if (usedMain >= 30) {
                lines.add(new Line(String.format(Locale.ROOT, "Inventory: %d/36 FULL", usedMain), w));
            } else if (usedMain >= 24) {
                lines.add(new Line(String.format(Locale.ROOT, "Inventory: %d/36", usedMain), c));
            }
        }

        if (HudConfig.showDeath && HudConfig.hasDeath) {
            lines.add(distanceLine("Death", HudConfig.deathX, HudConfig.deathY, HudConfig.deathZ,
                HudConfig.deathDim, player, mc, c));
        }

        if (HudConfig.showWaypoint && HudConfig.hasWaypoint) {
            lines.add(distanceLine(HudConfig.wpName, HudConfig.wpX, HudConfig.wpY, HudConfig.wpZ,
                HudConfig.wpDim, player, mc, c));
        }

        if (HudConfig.showFps) {
            lines.add(new Line("FPS: " + mc.getFps(), c));
        }

        return lines;
    }

    private static Line distanceLine(String label, double tx, double ty, double tz, String dim,
                                     LocalPlayer player, Minecraft mc, int color) {
        String currentDim = mc.level.dimension().identifier().toString();
        if (currentDim.equals(dim)) {
            double dx = tx - player.getX();
            double dy = ty - player.getY();
            double dz = tz - player.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            return new Line(String.format(Locale.ROOT, "%s: %.0f %.0f %.0f  (%.0fm)",
                label, tx, ty, tz, dist), color);
        }
        return new Line(String.format(Locale.ROOT, "%s: %.0f %.0f %.0f [%s]",
            label, tx, ty, tz, dim), color);
    }

    private static String armorLine(LocalPlayer player) {
        StringBuilder sb = new StringBuilder("Armor:");
        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
        String[] names = {"H", "C", "L", "F"};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = player.getItemBySlot(slots[i]);
            if (stack.isEmpty() || !stack.isDamageableItem()) {
                sb.append(' ').append(names[i]).append(":-");
            } else {
                int max = stack.getMaxDamage();
                int left = max - stack.getDamageValue();
                int pct = max <= 0 ? 100 : (left * 100 / max);
                sb.append(' ').append(names[i]).append(':').append(pct).append('%');
            }
        }
        return sb.toString();
    }

    private static int countItem(LocalPlayer player, net.minecraft.world.item.Item item) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }
}
