package org.chatterjay.network_tool_plus.mixin;

import java.lang.reflect.Field;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.ToolboxPanel;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Mixin(ToolboxPanel.class)
public abstract class ToolboxPanelMixin {

    private static final ResourceLocation NTP_EXTRA_PANELS = ResourceLocation.parse("network_tool_plus:textures/guis/extra_panels.png");

    private static Field NTP_IMAGES_FIELD;
    private static Field NTP_WIDGETS_FIELD;

    static {
        try {
            NTP_IMAGES_FIELD = ScreenStyle.class.getDeclaredField("images");
            NTP_IMAGES_FIELD.setAccessible(true);
            NTP_WIDGETS_FIELD = ScreenStyle.class.getDeclaredField("widgets");
            NTP_WIDGETS_FIELD.setAccessible(true);
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("HEAD"), remap = false)
    private static void networkToolPlus$fixToolbox(ScreenStyle style, Component toolbeltName, CallbackInfo ci) {
        if (NTP_IMAGES_FIELD == null || NTP_WIDGETS_FIELD == null)
            return;

        try {
            var images = (Map<String, Blitter>) NTP_IMAGES_FIELD.get(style);
            if (images != null) {
                var toolboxImage = Blitter.texture(NTP_EXTRA_PANELS, 160, 160)
                        .src(101, 24, 58, 138);
                images.put("toolbox", toolboxImage);
            }

            var widgets = (Map<String, WidgetStyle>) NTP_WIDGETS_FIELD.get(style);
            if (widgets != null) {
                var ws = widgets.get("toolbox");
                if (ws != null) {
                    ws.setHeight(138);
                }
            }
        } catch (Exception e) {
        }
    }
}
