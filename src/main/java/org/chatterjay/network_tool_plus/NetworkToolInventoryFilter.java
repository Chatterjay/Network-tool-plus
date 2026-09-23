package org.chatterjay.network_tool_plus;

import appeng.api.inventories.InternalInventory;
import appeng.util.inv.filter.IAEItemFilter;

import net.minecraft.world.item.ItemStack;

/** Applies the configurable storage rules to every network-tool inventory. */
public final class NetworkToolInventoryFilter implements IAEItemFilter {

    @Override
    public boolean allowExtract(InternalInventory inv, int slot, int amount) {
        return true;
    }

    @Override
    public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
        return NetworkToolConfig.allows(stack);
    }
}
