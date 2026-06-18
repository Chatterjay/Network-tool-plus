package org.chatterjay.network_tool_plus.integration;

import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CuriosRegistration {

    @SuppressWarnings("unused")
    public static void init(final FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("curios"))
            return;

        InterModComms.sendTo("curios", "register_type", () -> {
            try {
                var builderClass = Class.forName("top.theillusivec4.curios.api.SlotTypeMessage$Builder");
                var ctor = builderClass.getConstructor(String.class);
                Object builder = ctor.newInstance("network_tool");

                builder.getClass().getMethod("size", int.class).invoke(builder, 1);
                builder.getClass().getMethod("priority", int.class).invoke(builder, 200);

                var rlClass = Class.forName("net.minecraft.resources.ResourceLocation");
                var rlCtor = rlClass.getConstructor(String.class);
                Object icon = rlCtor.newInstance("curios:slot/empty_curio_slot");
                builder.getClass().getMethod("icon", rlClass).invoke(builder, icon);

                return builder.getClass().getMethod("build").invoke(builder);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
