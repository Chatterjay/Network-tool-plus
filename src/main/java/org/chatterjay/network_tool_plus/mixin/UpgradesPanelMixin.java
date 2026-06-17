package org.chatterjay.network_tool_plus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import appeng.client.gui.widgets.UpgradesPanel;

@Mixin(UpgradesPanel.class)
public abstract class UpgradesPanelMixin {

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 128), remap = false)
    private static int networkToolPlus$modifyTextureSize(int constant) {
        return 160;
    }
}
