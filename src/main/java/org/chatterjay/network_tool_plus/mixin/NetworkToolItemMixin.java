package org.chatterjay.network_tool_plus.mixin;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.chatterjay.network_tool_plus.integration.CuriosProxy;

import appeng.items.contents.NetworkToolMenuHost;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.tools.NetworkToolItem;
import appeng.menu.locator.MenuLocators;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Mixin(NetworkToolItem.class)
public abstract class NetworkToolItemMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Unique
    private static final String TAG_COLLECTOR_MODE = "collector_mode";

    @Inject(method = "onItemUseFirst", at = @At("HEAD"), cancellable = true, remap = false)
    private void networkToolPlus$onItemUseFirst(ItemStack stack, UseOnContext context,
            CallbackInfoReturnable<InteractionResult> cir) {
        LOGGER.info("[NetworkToolPlus] Mixin onItemUseFirst called, player={}, sneaking={}",
                context.getPlayer() != null ? context.getPlayer().getName().getString() : "null",
                context.getPlayer() != null && context.getPlayer().isSecondaryUseActive());
        if (context.getPlayer() != null && context.getPlayer().isSecondaryUseActive()) {
            if (!context.getLevel().isClientSide()) {
                networkToolPlus$toggleCollectorMode(stack, context.getPlayer());
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide()));
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = false)
    private void networkToolPlus$use(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        LOGGER.info("[NetworkToolPlus] Mixin use called, player={}, sneaking={}",
                player.getName().getString(), player.isSecondaryUseActive());
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                networkToolPlus$toggleCollectorMode(player.getItemInHand(hand), player);
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide()));
        }
    }

    @Unique
    private void networkToolPlus$toggleCollectorMode(ItemStack stack, Player player) {
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = data.copyTag();
        boolean currentMode = tag.getBoolean(TAG_COLLECTOR_MODE);
        LOGGER.info("[NetworkToolPlus] Toggle collector mode: current={}", currentMode);
        if (currentMode) {
            tag.putBoolean(TAG_COLLECTOR_MODE, false);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "§7[NetworkTool] §cCollector mode disabled"), true);
        } else {
            tag.putBoolean(TAG_COLLECTOR_MODE, true);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                "§7[NetworkTool] §aCollector mode enabled"), true);
            networkToolPlus$collectCards(stack, player);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Inject(method = "findNetworkToolInv", at = @At("RETURN"), cancellable = true, remap = false)
    private static void networkToolPlus$findNetworkToolInv(Player player,
            CallbackInfoReturnable<NetworkToolMenuHost> cir) {
        LOGGER.debug("[NetworkToolPlus] Mixin findNetworkToolInv called, currentReturn={}", cir.getReturnValue());
        if (cir.getReturnValue() != null)
            return;
        if (!CuriosProxy.isLoaded())
            return;

        ItemStack curiosStack = CuriosProxy.findAnyTool(player);
        if (!curiosStack.isEmpty()) {
            LOGGER.info("[NetworkToolPlus] Found tool in Curios slot for menu locator");
            var toolItem = (NetworkToolItem) curiosStack.getItem();
            var locator = MenuLocators.forStack(curiosStack);
            var host = new NetworkToolMenuHost<>(toolItem, player, locator, null);
            cir.setReturnValue(host);
        }
    }

    @Unique
    private void networkToolPlus$collectCards(ItemStack toolStack, Player player) {
        LOGGER.info("[NetworkToolPlus] Immediate collect on toggle for player: {}", player.getName().getString());
        var inv = NetworkToolItem.getInventory(toolStack);
        Inventory playerInv = player.getInventory();
        int collectedCount = 0;

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
                collectedCount++;
                slotStack.shrink(inserted);
                if (slotStack.isEmpty()) {
                    playerInv.setItem(i, ItemStack.EMPTY);
                } else {
                    playerInv.setItem(i, slotStack);
                }
                LOGGER.info("[NetworkToolPlus] Collected {}x {} on toggle", inserted, key.getPath());
            }
        }

        LOGGER.info("[NetworkToolPlus] Immediate collect complete: {} items collected", collectedCount);
    }
}
