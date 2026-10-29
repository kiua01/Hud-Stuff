package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
        SpeedTracker.register();
        ClientCommands.register();

        // Keep chunk borders forced on/off every tick (original stays visible while enabled)
        ClientTickEvents.END_CLIENT_TICK.register(client -> ChunkBorders.tick());

        // Enable borders by default like the Bedrock addon often does when the feature is on
        // User can toggle with .chunk — start OFF so we don't surprise players
        HudStuff.LOGGER.info("HUD Stuff ready — .chunk toggles borders (F3+G style), .hud help");
    }
}
