package ultralightui.platform

import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.TickEvent
import ultralightui.KeyBinding

object KeyBindingClientEvents {
    @JvmStatic
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(KeyBinding.MENU_KEY)
        event.register(KeyBinding.BROWSER_KEY)
    }

    @JvmStatic
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        KeyBinding.clientTick()
    }
}
