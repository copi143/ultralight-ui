package ultralightui.platform

import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import ultralightui.KeyBinding

object KeyBindingClientEvents {
    @JvmStatic
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        KeyBinding.ALL_KEYS.forEach {
            event.register(it)
        }
    }

    @JvmStatic
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        KeyBinding.clientTick()
    }

    @JvmStatic
    fun onClientSetup(event: FMLClientSetupEvent) {
        KeyBinding.onGameInited()
        KeyBinding.ALL_KEYS.forEach {
            println(it.name)
        }
    }

    init {
        KeyBinding.boundKeyOf = { it.key.value }
    }
}
