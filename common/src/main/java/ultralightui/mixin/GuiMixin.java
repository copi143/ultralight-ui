package ultralightui.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ultralightui.Ultralight;
import ultralightui.UltralightHotbar;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        if (Ultralight.INSTANCE.getShowFakeHotbar()) {
            UltralightHotbar.INSTANCE.render(partialTick, guiGraphics);
            ci.cancel();
        }
    }
}
