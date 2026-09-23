package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.chatterjay.network_tool_plus.NetworkToolInventoryFilter;

import appeng.api.inventories.InternalInventory;
import appeng.items.contents.NetworkToolMenuHost;
import appeng.util.inv.AppEngInternalInventory;

/** Applies the configurable storage rules to the network tool inventory. */
@Mixin(NetworkToolMenuHost.class)
public abstract class NetworkToolMenuHostMixin {

    @Inject(method = "getInventory", at = @At("RETURN"), remap = false)
    private void networkToolPlus$applyStorageRules(CallbackInfoReturnable<InternalInventory> cir) {
        if (cir.getReturnValue() instanceof AppEngInternalInventory inventory) {
            inventory.setFilter(new NetworkToolInventoryFilter());
        }
    }
}
