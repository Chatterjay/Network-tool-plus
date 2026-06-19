package org.chatterjay.network_tool_plus;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import org.chatterjay.network_tool_plus.integration.CuriosRegistration;
import org.slf4j.Logger;

@Mod(Network_tool_plus.MODID)
public class Network_tool_plus {

    public static final String MODID = "network_tool_plus";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Network_tool_plus(IEventBus modEventBus) {
        LOGGER.info("[NetworkToolPlus] Mod loading...");
        LOGGER.info("[NetworkToolPlus] Mixin configs system property: {}",
                System.getProperty("mixin.configs"));
        LOGGER.info("[NetworkToolPlus] Development environment: {}",
                !FMLLoader.isProduction());

        if (ModList.get().isLoaded("curios")) {
            LOGGER.info("[NetworkToolPlus] Curios detected, registering integration");
            modEventBus.addListener(CuriosRegistration::init);
        } else {
            LOGGER.info("[NetworkToolPlus] Curios not detected, skipping integration");
        }

        LOGGER.info("[NetworkToolPlus] Mod loaded successfully");
    }
}
