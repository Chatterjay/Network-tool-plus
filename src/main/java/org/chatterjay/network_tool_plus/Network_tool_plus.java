package org.chatterjay.network_tool_plus;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import org.chatterjay.network_tool_plus.integration.CuriosRegistration;

@Mod(Network_tool_plus.MODID)
public class Network_tool_plus {

    public static final String MODID = "network_tool_plus";

    public Network_tool_plus(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(CuriosRegistration::init);
    }
}
