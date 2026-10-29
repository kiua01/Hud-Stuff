package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;

public final class HudStuffClient implements ClientModInitializer {
    private static KeyMapping toggleKey;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(HudStuff.MOD_ID, "main")
    );

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyMapping(new KeyMapping(
            "key.hudstuff.toggle",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_H,
            CATEGORY
        ));

        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(HudStuff.MOD_ID, "overlay"),
            (graphics, deltaTracker) -> HudOverlay.render(graphics, deltaTracker.getGameTimeDeltaPartialTick(true))
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                HudConfig.toggle();
                if (client.player != null) {
                    client.player.displayClientMessage(
                        Component.translatable(
                            HudConfig.enabled ? "hudstuff.hud.enabled" : "hudstuff.hud.disabled"
                        ),
                        true
                    );
                }
            }
        });

        HudStuff.LOGGER.info("HUD Stuff client ready — press H to toggle overlay");
    }
}
