package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.items.tools.NetworkToolItem;
import appeng.menu.AEBaseMenu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

@Mixin(AEBaseMenu.class)
public abstract class NetworkToolLockMixin {

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void networkToolPlus$lockToolSlot(MenuType<?> menuType, int id, Inventory playerInventory, Object host,
            CallbackInfo ci) {
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            if (playerInventory.getItem(i).getItem() instanceof NetworkToolItem) {
                ((AEBaseMenu) (Object) this).lockPlayerInventorySlot(i);
            }
        }
    }
}
