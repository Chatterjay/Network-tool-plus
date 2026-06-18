package org.chatterjay.network_tool_plus;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import org.chatterjay.network_tool_plus.integration.CuriosProxy;

import appeng.items.materials.UpgradeCardItem;
import appeng.items.tools.NetworkToolItem;
import appeng.menu.me.networktool.NetworkToolMenu;

@EventBusSubscriber(modid = Network_tool_plus.MODID, bus = EventBusSubscriber.Bus.GAME)
public class NetworkToolEvents {

    private static final int COLLECTION_INTERVAL = 10;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide())
            return;

        if (player.containerMenu instanceof NetworkToolMenu)
            return;
        if (player.tickCount % COLLECTION_INTERVAL != 0)
            return;

        ItemStack toolStack = findActiveTool(player);
        if (!toolStack.isEmpty())
            collectCards(toolStack, player);
    }

    private static ItemStack findActiveTool(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty())
                continue;
            if (!(stack.getItem() instanceof NetworkToolItem))
                continue;
            var data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.copyTag().getBoolean("collector_mode"))
                return stack;
        }

        if (CuriosProxy.isLoaded()) {
            ItemStack curiosStack = CuriosProxy.findActiveTool(player);
            if (!curiosStack.isEmpty())
                return curiosStack;
        }

        return ItemStack.EMPTY;
    }

    private static void collectCards(ItemStack toolStack, Player player) {
        var inv = NetworkToolItem.getInventory(toolStack);
        Inventory playerInv = player.getInventory();
        boolean changed = false;

        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack slotStack = playerInv.getItem(i);
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
            var overflow = inv.addItems(slotStack.copy());
            int inserted = originalCount - overflow.getCount();

            if (inserted > 0) {
                slotStack.shrink(inserted);
                if (slotStack.isEmpty()) {
                    playerInv.setItem(i, ItemStack.EMPTY);
                }
                changed = true;
            }
        }

        if (changed) {
            CuriosProxy.syncStack(player, toolStack);
            player.containerMenu.broadcastChanges();
        }
    }
}
