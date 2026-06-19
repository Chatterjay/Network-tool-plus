package org.chatterjay.network_tool_plus.mixin;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;

import net.minecraft.resources.ResourceLocation;

@Mixin(StyleManager.class)
public abstract class ScreenStyleMixin {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    @Unique
    private static final ResourceLocation TOOLBOX_BG = ResourceLocation.parse("network_tool_plus:textures/guis/toolbox.png");
    @Unique
    private static final ResourceLocation EXTRA_PANELS = ResourceLocation.parse("network_tool_plus:textures/guis/extra_panels.png");

    @Inject(method = "loadStyleDocInternal", at = @At("RETURN"), remap = false)
    private static void networkToolPlus$patchBackground(String path, CallbackInfoReturnable<ScreenStyle> cir) {
        ScreenStyle style = cir.getReturnValue();
        if (style == null) return;

        try {
            var imagesField = ScreenStyle.class.getDeclaredField("images");
            imagesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var images = (java.util.Map<String, Blitter>) imagesField.get(style);

            if (images != null && images.containsKey("toolbox")) {
                var toolboxImage = Blitter.texture(EXTRA_PANELS, 160, 160)
                        .src(101, 24, 58, 136);
                images.put("toolbox", toolboxImage);

                var widgetsField = ScreenStyle.class.getDeclaredField("widgets");
                widgetsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                var widgets = (java.util.Map<String, appeng.client.gui.style.WidgetStyle>) widgetsField.get(style);
                if (widgets != null && widgets.containsKey("toolbox")) {
                    var ws = widgets.get("toolbox");
                    ws.setWidth(58);
                    ws.setHeight(138);
                }
                LOGGER.info("[NetworkToolPlus] Patched toolbox image for {}", path);
            }

            if ("/screens/network_tool.json".equals(path)) {
                var backgroundField = ScreenStyle.class.getDeclaredField("background");
                backgroundField.setAccessible(true);
                backgroundField.set(style, Blitter.texture(TOOLBOX_BG, 256, 256).src(0, 0, 175, 168));
                LOGGER.info("[NetworkToolPlus] PATCHED background for {}", path);
            }
        } catch (Exception e) {
            LOGGER.error("[NetworkToolPlus] ERROR in patchBackground: {}", e.toString());
            for (var ste : e.getStackTrace()) {
                LOGGER.error("  at {}", ste.toString());
            }
        }
    }
}