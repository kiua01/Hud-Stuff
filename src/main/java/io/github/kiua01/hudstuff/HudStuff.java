package io.github.kiua01.hudstuff;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HudStuff implements ModInitializer {
    public static final String MOD_ID = "hudstuff";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("HUD Stuff initialized (26.3)");
    }
}
