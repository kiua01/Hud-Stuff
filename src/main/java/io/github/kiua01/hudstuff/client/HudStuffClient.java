package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class HudStuffClient implements ClientModInitializer {
    private static int saveCooldown = 0;
    private static boolean dirty = false;

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

        // Load saved config once the client is up
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ChunkBorders.tick();

            // First tick: load
            if (client.player != null && !loadedOnce) {
                ConfigIO.load();
                loadedOnce = true;
            }

            // Debounced auto-save
            if (dirty) {
                saveCooldown++;
                if (saveCooldown > 40) { // ~2 seconds
                    ConfigIO.save();
                    dirty = false;
                    saveCooldown = 0;
                }
            }
        });

        HudStuff.LOGGER.info("HUD Stuff ready — config auto-saves to config/hudstuff.properties");
    }

    private static boolean loadedOnce = false;

    /** Mark config dirty so it will be saved shortly. */
    public static void markDirty() {
        dirty = true;
        saveCooldown = 0;
    }
}
