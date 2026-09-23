package org.chatterjay.network_tool_plus;

import java.util.List;
import java.util.Locale;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

import appeng.api.upgrades.Upgrades;

/** Server-side rules for items that may be stored in a network tool. */
public final class NetworkToolConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        var pair = new ForgeConfigSpec.Builder().configure(Values::new);
        var values = pair.getLeft();
        WHITELIST = values.whitelist;
        BLACKLIST = values.blacklist;
        SPEC = pair.getRight();
    }

    private static final class Values {
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;

        private Values(ForgeConfigSpec.Builder builder) {
            builder.comment(
                "Controls which AE2 upgrade cards can be inserted into Network Tools.",
                "Entries use item registry names, for example \"ae2:speed_card\".",
                "An empty whitelist allows every upgrade card; blacklist entries always take precedence.")
                .push("network_tool");

            whitelist = builder.comment(
                "Only upgrade cards listed here may be inserted. Leave empty to allow all upgrade cards.")
                .defineListAllowEmpty("whitelist", List.of(), NetworkToolConfig::isValidItemId);

            blacklist = builder.comment(
                "Upgrade cards listed here are never inserted, even when they are in the whitelist.")
                .defineListAllowEmpty("blacklist", List.of(), NetworkToolConfig::isValidItemId);

            builder.pop();
        }
    }

    private static boolean isValidItemId(Object value) {
        if (!(value instanceof String itemId)) {
            return false;
        }
        return ResourceLocation.tryParse(itemId.trim()) != null;
    }

    /**
     * Returns whether a stack is an upgrade card allowed by the configured rules.
     *
     * <p>The config is synced to clients by the loader, so both sides agree on the rules.</p>
     */
    public static boolean allows(ItemStack stack) {
        if (stack.isEmpty() || !Upgrades.isUpgradeCardItem(stack.getItem())) {
            return false;
        }

        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) {
            return false;
        }

        var id = itemId.toString().toLowerCase(Locale.ROOT);
        var whitelist = getEntries(WHITELIST);
        if (!whitelist.isEmpty() && !contains(whitelist, id)) {
            return false;
        }

        return !contains(getEntries(BLACKLIST), id);
    }

    private static List<? extends String> getEntries(ForgeConfigSpec.ConfigValue<List<? extends String>> value) {
        try {
            var entries = value.get();
            return entries == null ? List.of() : entries;
        } catch (IllegalStateException ignored) {
            // Config values can be queried while a client is still starting up.
            return List.of();
        }
    }

    private static boolean contains(List<? extends String> entries, String itemId) {
        for (var entry : entries) {
            if (entry != null && entry.trim().toLowerCase(Locale.ROOT).equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    private NetworkToolConfig() {
    }
}
