package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.menu.ToolboxMenu;

@Mixin(ToolboxMenu.class)
public abstract class ToolboxMenuTickMixin {

    @Shadow(remap = false)
    private NetworkToolMenuHost inv;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void networkToolPlus$tick(CallbackInfo ci) {
        if (inv != null && inv.getSlot() == null) {
            ci.cancel();
        }
    }
}
