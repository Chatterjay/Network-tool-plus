package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.UpgradesPanel;

import net.minecraft.resources.ResourceLocation;

@Mixin(UpgradesPanel.class)
public abstract class UpgradesPanelMixin {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lappeng/client/gui/style/Blitter;texture(Ljava/lang/String;II)Lappeng/client/gui/style/Blitter;"), remap = false)
    private static Blitter networkToolPlus$redirectTexture(String path, int w, int h) {
        return Blitter.texture(ResourceLocation.parse("network_tool_plus:textures/guis/extra_panels.png"), 160, 160);
    }
}