package ultralightui

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import ultralightui.platform.KeyBindingClientEvents

@Mod(Constants.MOD_ID)
class UltralightUIForge() {
    init {
        Constants.LOG.info("Hello Forge world from Kotlin!")
        CommonObject.init()
        Ultralight.init()
        KotlinModLoadingContext.get().getKEventBus().addListener(KeyBindingClientEvents::onRegisterKeyMappings)
        MinecraftForge.EVENT_BUS.addListener(KeyBindingClientEvents::onClientTick)
    }
}
