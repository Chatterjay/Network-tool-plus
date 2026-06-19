package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.UpgradesPanel;

import net.minecraft.resources.ResourceLocation;

@Mixin(UpgradesPanel.class)
public abstract class UpgradesPanelMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void networkToolPlus$patchBackground(CallbackInfo ci) {
        try {
            var bgField = UpgradesPanel.class.getDeclaredField("BACKGROUND");
            bgField.setAccessible(true);
            bgField.set(null, Blitter.texture(
                ResourceLocation.parse("network_tool_plus:textures/guis/extra_panels.png"), 160, 160));

            var icField = UpgradesPanel.class.getDeclaredField("INNER_CORNER");
            icField.setAccessible(true);
            icField.set(null, Blitter.texture(
                ResourceLocation.parse("network_tool_plus:textures/guis/extra_panels.png"), 160, 160)
                .src(12, 33, 18, 18));
        } catch (Exception e) {
        }
    }
}