package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.items.tools.NetworkToolItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isFoil", at = @At("RETURN"), cancellable = true)
    private void networkToolPlus$isFoil(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof NetworkToolItem) {
            var data = self.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.copyTag().getBoolean("collector_mode")) {
                cir.setReturnValue(true);
            }
        }
    }
}
