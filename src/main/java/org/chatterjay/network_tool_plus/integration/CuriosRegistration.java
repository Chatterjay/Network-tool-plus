package org.chatterjay.network_tool_plus.integration;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CuriosRegistration {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unused")
    public static void init(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            LOGGER.info("[NetworkToolPlus] Curios integration ready");
        }
    }
}