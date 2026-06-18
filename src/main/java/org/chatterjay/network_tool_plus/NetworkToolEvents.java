package org.chatterjay.network_tool_plus;

import java.lang.reflect.Field;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.chatterjay.network_tool_plus.integration.CuriosProxy;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.tools.NetworkToolItem;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.networktool.NetworkToolMenu;

@Mod.EventBusSubscriber(modid = Network_tool_plus.MODID)
public class NetworkToolEvents {

    private static final int COLLECTION_INTERVAL = 10;
    private static final Field TOOLBOX_INV_FIELD;

    static {
        Field f = null;
        try {
            f = ToolboxMenu.class.getDeclaredField("inv");
            f.setAccessible(true);
        } catch (Exception e) {
        }
        TOOLBOX_INV_FIELD = f;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (event.player.level().isClientSide())
            return;

        Player player = event.player;

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
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean("collector_mode"))
                return stack;
        }

        if (CuriosProxy.isLoaded()) {
            ItemStack curiosStack = CuriosProxy.findActiveTool(player);
            if (!curiosStack.isEmpty())
                return curiosStack;
        }

        return ItemStack.EMPTY;
    }

    private static NetworkToolMenuHost resolveToolHost(Player player) {
        ToolboxMenu toolbox = null;
        if (player.containerMenu instanceof MEStorageMenu storageMenu) {
            toolbox = storageMenu.getToolbox();
        } else if (player.containerMenu instanceof UpgradeableMenu<?> upgradeableMenu) {
            toolbox = upgradeableMenu.getToolbox();
        }
        if (toolbox != null && toolbox.isPresent() && TOOLBOX_INV_FIELD != null) {
            try {
                return (NetworkToolMenuHost) TOOLBOX_INV_FIELD.get(toolbox);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static void collectCards(ItemStack toolStack, Player player) {
        NetworkToolMenuHost toolHost = resolveToolHost(player);
        boolean useToolbox = toolHost != null;
        if (toolHost == null) {
            toolHost = new NetworkToolMenuHost(player, null, toolStack, null);
        }

        Inventory inv = player.getInventory();
        boolean changed = false;

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
                changed = true;
            }
        }

        if (changed) {
            toolHost.saveChanges();
            CuriosProxy.syncStack(player, toolStack);
            if (!useToolbox) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    if (player.getInventory().getItem(i) == toolStack) {
                        player.getInventory().setItem(i, toolStack.copy());
                        break;
                    }
                }
            }
            player.containerMenu.broadcastChanges();
        }
    }
}
