package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.menu.ToolboxMenu;
import appeng.menu.me.networktool.NetworkToolMenu;

public class NetworkToolSlotCountMixin {

    @Mixin(NetworkToolMenuHost.class)
    public static class NetworkToolMenuHostMixin {
        @ModifyConstant(method = "<init>", constant = @Constant(intValue = 9), remap = false)
        private int networkToolPlus$modifySlotCount(int constant) {
            return 18;
        }
    }

    @Mixin(ToolboxMenu.class)
    public static class ToolboxMenuMixin {
        @ModifyConstant(method = "<init>", constant = @Constant(intValue = 9), remap = false)
        private int networkToolPlus$modifySlotCount(int constant) {
            return 18;
        }
    }

    @Mixin(NetworkToolMenu.class)
    public static class NetworkToolMenuMixin {
        @ModifyConstant(method = "<init>", constant = @Constant(intValue = 9), remap = false)
        private int networkToolPlus$modifySlotCount(int constant) {
            return 18;
        }
    }
}
