package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import appeng.items.tools.NetworkToolItem;
import appeng.menu.me.networktool.NetworkToolMenu;

public class NetworkToolSlotCountMixin {

    @Mixin(NetworkToolItem.class)
    public static class NetworkToolInventoryMixin {
        @ModifyConstant(method = "getInventory", constant = @Constant(intValue = 9), remap = false)
        private static int networkToolPlus$modifyInventorySize(int constant) {
            return 21;
        }
    }

    @Mixin(NetworkToolMenu.class)
    public static class NetworkToolMenuMixin {
        @ModifyConstant(method = "<init>", constant = @Constant(intValue = 9), remap = false)
        private int networkToolPlus$modifySlotCount(int constant) {
            return 21;
        }
    }
}