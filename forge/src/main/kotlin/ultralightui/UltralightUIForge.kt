package ultralightui

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import ultralightui.platform.KeyBindingClientEvents

@Mod(Constants.MOD_ID)
class UltralightUIForge() {
    init {
        Constants.LOG.info("Hello Forge world from Kotlin!")
        CommonObject.init()
        Ultralight.init()
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Ultralight.clientInit()
            KotlinModLoadingContext.get().getKEventBus().addListener(KeyBindingClientEvents::onRegisterKeyMappings)
            MinecraftForge.EVENT_BUS.addListener(KeyBindingClientEvents::onClientTick)
            MinecraftForge.EVENT_BUS.addListener(KeyBindingClientEvents::onClientSetup)
        } else {
            Ultralight.serverInit()
        }
    }
}
