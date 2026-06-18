package org.chatterjay.network_tool_plus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.chatterjay.network_tool_plus.integration.CuriosRegistration;

@Mod(Network_tool_plus.MODID)
public class Network_tool_plus {

    public static final String MODID = "network_tool_plus";

    public Network_tool_plus() {
        MinecraftForge.EVENT_BUS.register(this);
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(CuriosRegistration::init);
    }
}
