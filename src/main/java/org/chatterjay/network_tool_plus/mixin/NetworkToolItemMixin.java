package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.tools.NetworkToolItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Mixin(NetworkToolItem.class)
public abstract class NetworkToolItemMixin {

    @Unique
    private static final String TAG_COLLECTOR_MODE = "collector_mode";

    @Inject(method = "onItemUseFirst", at = @At("HEAD"), cancellable = true, remap = false)
    private void networkToolPlus$onItemUseFirst(ItemStack stack, UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getPlayer() != null && context.getPlayer().isSecondaryUseActive()) {
            if (!context.getLevel().isClientSide()) {
                networkToolPlus$toggleCollectorMode(stack, context.getPlayer());
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide()));
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void networkToolPlus$use(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                networkToolPlus$toggleCollectorMode(player.getItemInHand(hand), player);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide()));
        }
    }

    @Unique
    private void networkToolPlus$toggleCollectorMode(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        boolean currentMode = tag.getBoolean(TAG_COLLECTOR_MODE);
        if (currentMode) {
            tag.putBoolean(TAG_COLLECTOR_MODE, false);
        } else {
            tag.putBoolean(TAG_COLLECTOR_MODE, true);
            networkToolPlus$collectCards(stack, player);
        }
    }

    @Unique
    private void networkToolPlus$collectCards(ItemStack toolStack, Player player) {
        var toolHost = new NetworkToolMenuHost(player, null, toolStack, null);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack slotStack = inv.getItem(i);
            if (slotStack.isEmpty())
                continue;
            if (slotStack.getItem() instanceof NetworkToolItem)
                continue;

            var key = BuiltInRegistries.ITEM.getKey(slotStack.getItem());
            if (key == null || !"ae2".equals(key.getNamespace()))
                continue;
            if (!(slotStack.getItem() instanceof UpgradeCardItem))
                continue;

            int originalCount = slotStack.getCount();
            var overflow = toolHost.getInventory().addItems(slotStack.copy());
            int inserted = originalCount - overflow.getCount();

            if (inserted > 0) {
                slotStack.shrink(inserted);
                if (slotStack.isEmpty()) {
                    inv.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        toolHost.saveChanges();
    }
}
