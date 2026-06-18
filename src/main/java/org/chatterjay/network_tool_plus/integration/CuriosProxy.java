package org.chatterjay.network_tool_plus.integration;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.fml.ModList;

import appeng.items.tools.NetworkToolItem;

import java.lang.reflect.Method;
import java.util.Optional;

public class CuriosProxy {
    private static Boolean loaded;
    private static Method getCuriosMethod;
    private static Method getStacksMethod;
    private static Method getStackInSlotMethod;
    private static Method setStackInSlotMethod;
    private static Method getSlotsMethod;

    public static boolean isLoaded() {
        if (loaded == null) {
            var modList = ModList.get();
            loaded = modList != null && modList.isLoaded("curios");
        }
        return loaded;
    }

    private static Object getHandler(Player player) {
        if (!isLoaded())
            return null;
        try {
            var curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            var getCuriosInventory = curiosApi.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
            Object lazyOpt = getCuriosInventory.invoke(null, player);

            var lazyOptClass = Class.forName("net.neoforged.neoforge.common.util.LazyOptional");
            if (lazyOptClass == null)
                lazyOptClass = Class.forName("net.minecraftforge.common.util.LazyOptional");
            boolean isPresent = (boolean) lazyOptClass.getMethod("isPresent").invoke(lazyOpt);
            if (!isPresent)
                return null;

            var resolveMethod = lazyOptClass.getMethod("resolve");
            Optional<?> opt = (Optional<?>) resolveMethod.invoke(lazyOpt);
            return opt.orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean hasCollectorMode(ItemStack stack) {
        var data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBoolean("collector_mode");
    }

    public static ItemStack findActiveTool(Player player) {
        return findTool(player, true);
    }

    public static ItemStack findAnyTool(Player player) {
        return findTool(player, false);
    }

    private static ItemStack findTool(Player player, boolean requireCollectorMode) {
        Object handler = getHandler(player);
        if (handler == null)
            return ItemStack.EMPTY;

        try {
            if (getCuriosMethod == null)
                getCuriosMethod = handler.getClass().getMethod("getCurios");
            Object curiosMap = getCuriosMethod.invoke(handler);

            for (var entry : ((java.util.Map<?, ?>) curiosMap).entrySet()) {
                Object stacksHandler = entry.getValue();

                if (getStacksMethod == null)
                    getStacksMethod = stacksHandler.getClass().getMethod("getStacks");
                Object stackHandler = getStacksMethod.invoke(stacksHandler);

                if (getSlotsMethod == null)
                    getSlotsMethod = stackHandler.getClass().getMethod("getSlots");
                int slots = (int) getSlotsMethod.invoke(stackHandler);

                if (getStackInSlotMethod == null)
                    getStackInSlotMethod = stackHandler.getClass().getMethod("getStackInSlot", int.class);

                for (int i = 0; i < slots; i++) {
                    ItemStack stack = (ItemStack) getStackInSlotMethod.invoke(stackHandler, i);
                    if (!stack.isEmpty() && stack.getItem() instanceof NetworkToolItem) {
                        if (!requireCollectorMode || hasCollectorMode(stack))
                            return stack;
                    }
                }
            }
        } catch (Exception e) {
        }
        return ItemStack.EMPTY;
    }

    public static void syncStack(Player player, ItemStack updated) {
        Object handler = getHandler(player);
        if (handler == null)
            return;

        try {
            if (getCuriosMethod == null)
                getCuriosMethod = handler.getClass().getMethod("getCurios");
            Object curiosMap = getCuriosMethod.invoke(handler);

            for (var entry : ((java.util.Map<?, ?>) curiosMap).entrySet()) {
                Object stacksHandler = entry.getValue();

                if (getStacksMethod == null)
                    getStacksMethod = stacksHandler.getClass().getMethod("getStacks");
                Object stackHandler = getStacksMethod.invoke(stacksHandler);

                if (getSlotsMethod == null)
                    getSlotsMethod = stackHandler.getClass().getMethod("getSlots");
                int slots = (int) getSlotsMethod.invoke(stackHandler);

                if (getStackInSlotMethod == null)
                    getStackInSlotMethod = stackHandler.getClass().getMethod("getStackInSlot", int.class);

                for (int i = 0; i < slots; i++) {
                    ItemStack stack = (ItemStack) getStackInSlotMethod.invoke(stackHandler, i);
                    if (stack == updated) {
                        if (setStackInSlotMethod == null)
                            setStackInSlotMethod = stackHandler.getClass().getMethod("setStackInSlot", int.class, ItemStack.class);
                        setStackInSlotMethod.invoke(stackHandler, i, updated);
                        return;
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
