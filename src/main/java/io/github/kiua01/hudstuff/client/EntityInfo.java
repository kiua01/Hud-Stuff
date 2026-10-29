package io.github.kiua01.hudstuff.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Locale;

/** Crosshair entity info (health, type) — ports mob_damage / entity HUD lines. */
public final class EntityInfo {
    private EntityInfo() {}

    public static String line(Minecraft mc, LocalPlayer player) {
        if (!HudConfig.showEntity) return null;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) return null;
        Entity e = ((EntityHitResult) mc.hitResult).getEntity();
        if (e == null) return null;

        String id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
        double dist = player.distanceTo(e);

        if (e instanceof LivingEntity living) {
            float hp = living.getHealth();
            float max = living.getMaxHealth();
            String kind = living instanceof Enemy ? "hostile"
                : living instanceof Animal ? "animal" : "living";
            return String.format(Locale.ROOT, "Entity: %s  %.1f/%.1f HP  %s  %.1fm",
                id, hp, max, kind, dist);
        }
        return String.format(Locale.ROOT, "Entity: %s  %.1fm", id, dist);
    }
}
