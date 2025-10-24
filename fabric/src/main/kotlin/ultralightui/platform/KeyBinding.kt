package ultralightui.platform

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.Minecraft
import ultralightui.KeyBinding

fun setupKeyBinding() {
    KeyBinding.ALL_KEYS.forEach {
        KeyBindingHelper.registerKeyBinding(it)
    }

    ClientTickEvents.END_CLIENT_TICK.register { client ->
        KeyBinding.clientTick()
    }

    ClientLifecycleEvents.CLIENT_STARTED.register {
        KeyBinding.onGameInited()
        Minecraft.getInstance().options.keyMappings.forEach {
            println(it.name)
        }
    }

    KeyBinding.boundKeyOf = { KeyBindingHelper.getBoundKeyOf(it).value }
}
