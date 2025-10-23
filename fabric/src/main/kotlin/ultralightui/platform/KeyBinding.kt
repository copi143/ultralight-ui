package ultralightui.platform

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.Minecraft
import ultralightui.KeyBinding

fun setupKeyBinding() {
    KeyBindingHelper.registerKeyBinding(KeyBinding.MENU_KEY)
    KeyBindingHelper.registerKeyBinding(KeyBinding.BROWSER_KEY)
    KeyBindingHelper.registerKeyBinding(KeyBinding.PRINTSCREEN_KEY)

    ClientTickEvents.END_CLIENT_TICK.register { client ->
        KeyBinding.clientTick()
    }

    ClientLifecycleEvents.CLIENT_STARTED.register {
        Minecraft.getInstance().options.keyMappings.forEach {
            println(it.name)
        }
    }
}
