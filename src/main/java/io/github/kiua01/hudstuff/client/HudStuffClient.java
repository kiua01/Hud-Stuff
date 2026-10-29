package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class HudStuffClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(HudStuff.MOD_ID, "overlay"),
            (graphics, deltaTracker) -> HudOverlay.render(graphics)
        );

        DeathTracker.register();

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            ChunkBorderRenderer.render(
                context.matrixStack(),
                context.consumers(),
                context.tickCounter().getGameTimeDeltaPartialTick(true)
            );
        });

        HudStuff.LOGGER.info("HUD Stuff client ready — overlay + chunk borders + death waypoint");
    }
}
