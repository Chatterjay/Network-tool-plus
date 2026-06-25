package org.chatterjay.network_tool_plus.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.items.materials.UpgradeCardItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(AEBaseMenu.class)
public abstract class NetworkToolQuickMoveMixin {

    @Shadow(remap = false)
    public abstract boolean isClientSide();

    @Shadow(remap = false)
    public abstract SlotSemantic getSlotSemantic(Slot slot);

    @Shadow(remap = false)
    public abstract List<Slot> getSlots(SlotSemantic semantic);

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void networkToolPlus$quickMoveUpgradeCard(Player player, int idx,
            CallbackInfoReturnable<ItemStack> cir) {
        if (isClientSide()) return;

        var clickSlot = player.containerMenu.getSlot(idx);
        var stackToMove = clickSlot.getItem();
        if (stackToMove.isEmpty()) return;
        if (!(stackToMove.getItem() instanceof UpgradeCardItem)) return;

        var slotSemantic = getSlotSemantic(clickSlot);
        if (slotSemantic == SlotSemantics.UPGRADE) return;

        var upgradeSlots = getSlots(SlotSemantics.UPGRADE);
        if (upgradeSlots.isEmpty()) {
            return;
        }

        int originalCount = stackToMove.getCount();
        var remaining = stackToMove.copy();

        for (var upgradeSlot : upgradeSlots) {
            if (remaining.isEmpty()) break;
            remaining = upgradeSlot.safeInsert(remaining);
        }

        int inserted = originalCount - remaining.getCount();
        if (inserted <= 0) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        stackToMove.shrink(inserted);
        // Always call set() to trigger AppEngInternalInventory.saveChangedInventory
        // and persist the count change to the tool's CONTAINER data component.
        // Without this, partial removals (e.g., 64→63) leave the data component stale,
        // causing auto-collect to re-add cards from a phantom state.
        clickSlot.set(stackToMove.isEmpty() ? ItemStack.EMPTY : stackToMove);
        player.containerMenu.broadcastChanges();
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
