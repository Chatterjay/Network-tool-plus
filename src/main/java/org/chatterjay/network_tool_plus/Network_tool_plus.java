package org.chatterjay.network_tool_plus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(Network_tool_plus.MODID)
public class Network_tool_plus {

    public static final String MODID = "network_tool_plus";

    public Network_tool_plus() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
