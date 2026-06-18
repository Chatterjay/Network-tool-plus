package org.chatterjay.network_tool_plus.mixin;

import java.lang.reflect.Field;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.client.gui.AEBaseScreen;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.networktool.NetworkToolMenu;

import net.minecraft.world.inventory.Slot;

@Mixin(AEBaseScreen.class)
public abstract class NetworkToolScreenSlotsMixin {

    @Unique
    private static final int NTP_COLUMNS = 7;
    @Unique
    private static final int NTP_SLOT_SIZE = 18;
    @Unique
    private static Field NTP_SLOT_X;
    @Unique
    private static Field NTP_SLOT_Y;

    static {
        try {
            NTP_SLOT_X = Slot.class.getDeclaredField("x");
            NTP_SLOT_X.setAccessible(true);
            NTP_SLOT_Y = Slot.class.getDeclaredField("y");
            NTP_SLOT_Y.setAccessible(true);
        } catch (Exception e) {
        }
    }

    @Inject(method = "repositionSlots", at = @At("HEAD"), cancellable = true, remap = false)
    private void networkToolPlus$repositionSlots(SlotSemantic semantic, CallbackInfo ci) {
        if (semantic != SlotSemantics.STORAGE)
            return;

        var menu = ((AEBaseScreen<?>) (Object) this).getMenu();
        if (!(menu instanceof NetworkToolMenu))
            return;

        // GUI-relative positions: 2 columns left of original 3×3 start (62)
        int baseX = 62 - 2 * NTP_SLOT_SIZE;
        int baseY = 19;

        List<Slot> slots = ((AEBaseMenu) menu).getSlots(semantic);
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            int newX = baseX + (i % NTP_COLUMNS) * NTP_SLOT_SIZE;
            int newY = baseY + (i / NTP_COLUMNS) * NTP_SLOT_SIZE;
            if (NTP_SLOT_X != null && NTP_SLOT_Y != null) {
                try {
                    NTP_SLOT_X.setInt(slot, newX);
                    NTP_SLOT_Y.setInt(slot, newY);
                } catch (Exception e) {
                }
            }
        }

        ci.cancel();
    }
}
