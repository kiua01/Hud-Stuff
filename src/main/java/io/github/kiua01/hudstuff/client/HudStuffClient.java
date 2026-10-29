package io.github.kiua01.hudstuff.client;

import io.github.kiua01.hudstuff.HudStuff;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class HudStuffClient implements ClientModInitializer {
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.hudstuff.toggle",
            GLFW.GLFW_KEY_H,
            "key.hudstuff.category"
        ));

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            HudOverlay.render(graphics, tickCounter.getGameTimeDeltaPartialTick(true));
        });

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
