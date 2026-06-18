package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;

@Mixin(ToolboxMenu.class)
public abstract class ToolboxMenuLockMixin {

    @Shadow(remap = false)
    private NetworkToolMenuHost inv;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/menu/AEBaseMenu;lockPlayerInventorySlot(I)V"), remap = false)
    private void networkToolPlus$redirectLock(AEBaseMenu menu, int slot) {
        if (inv != null && inv.getSlot() == null)
            return;
        menu.lockPlayerInventorySlot(slot);
    }
}
