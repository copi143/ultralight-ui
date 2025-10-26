package ultralightui.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ultralightui.Constants;
import ultralightui.Ultralight;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "close", at = @At("TAIL"))
    private void onClosed(CallbackInfo ci) {
        var logger = Constants.getLOG();
        logger.info("Minecraft is closing, shutting down Ultralight...");
        Ultralight.lib.ultralightui_exit();
        logger.info("Ultralight shut down successfully.");
    }
}
