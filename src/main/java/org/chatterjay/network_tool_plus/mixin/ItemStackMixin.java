package org.chatterjay.network_tool_plus.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.items.tools.NetworkToolItem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hasFoil", at = @At("RETURN"), cancellable = true)
    private void networkToolPlus$hasFoil(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof NetworkToolItem) {
            CompoundTag tag = self.getTag();
            if (tag != null && tag.getBoolean("collector_mode")) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void networkToolPlus$tooltip(CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() instanceof NetworkToolItem) {
            CompoundTag tag = self.getTag();
            if (tag != null && tag.getBoolean("collector_mode")) {
                var lines = cir.getReturnValue();
                lines.add(Component.translatable("tooltip.network_tool_plus.collector_mode"));
            }
        }
    }
}
