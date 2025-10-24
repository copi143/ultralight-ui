package ultralightui

import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

object KeyBinding {
    val MENU_KEY = KeyMapping(
        "key.ultralightui.menu",
        GLFW.GLFW_KEY_SEMICOLON,
        "key.categories.ultralightui",
    )

    val BROWSER_KEY = KeyMapping(
        "key.ultralightui.browser",
        GLFW.GLFW_KEY_B,
        "key.categories.ultralightui",
    )

    val PRINTSCREEN_KEY = KeyMapping(
        "key.ultralightui.printscreen",
        GLFW.GLFW_KEY_PRINT_SCREEN,
        "key.categories.ultralightui",
    )

    val TEMPORARY_ESCAPE_KEY = KeyMapping(
        "key.ultralightui.temporary_escape",
        GLFW.GLFW_KEY_R,
        "key.categories.ultralightui",
    )

    val ALL_KEYS = listOf(MENU_KEY, BROWSER_KEY, PRINTSCREEN_KEY, TEMPORARY_ESCAPE_KEY)

    var boundKeyOf: (KeyMapping) -> Int = { _ -> -1 }

    private var prevGlfwKeyCallback: GLFWKeyCallback? = null
    fun onGameInited() {
        val window = Minecraft.getInstance().window.window
        prevGlfwKeyCallback = GLFW.glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == boundKeyOf(TEMPORARY_ESCAPE_KEY)) {
                if (Minecraft.getInstance().screen == null && action == GLFW.GLFW_PRESS) {
                    Minecraft.getInstance().setScreen(TemporaryEscapeScreen())
                } else if (Minecraft.getInstance().screen is TemporaryEscapeScreen && action == GLFW.GLFW_RELEASE) {
                    Minecraft.getInstance().setScreen(null)
                } else {
                    prevGlfwKeyCallback?.invoke(window, key, scancode, action, mods)
                }
            }else {
                prevGlfwKeyCallback?.invoke(window, key, scancode, action, mods)
            }
        }
    }

    fun clientTick() {
        if (Minecraft.getInstance().screen == null && MENU_KEY.isDown) {
            openMenu()
        }
        if (Minecraft.getInstance().screen == null && BROWSER_KEY.isDown) {
            openBrowser("https://html.plos-clan.org/background/color#38bcce79")
        }
        if (PRINTSCREEN_KEY.isDown) {
            println("UltralightUI: Taking screenshot...")
            printScreen()
        }
    }

    fun openMenu() {
        Minecraft.getInstance().setScreen(UltralightScreen())
    }

    fun openBrowser(url: String) { // Minecraft.getInstance().setScreen(UltralightBrowserScreen(url))
        printScreen()
    }

    fun printScreen() {
        val renderTarget = Minecraft.getInstance().mainRenderTarget
        val width = renderTarget.width
        val height = renderTarget.height
        val texture = renderTarget.colorTextureId
        val buffer = Ultralight.lib.ultralightui_alloc((width * height * 4).toLong())
        downloadTexture(texture, buffer, width, height)
        if (Ultralight.lib.ultralightui_save_to_png("ultralight/screenshot.png", buffer, width, height) == 0) {
            Minecraft.getInstance().setScreen(UltralightBrowserScreen("file://screenshot.html"))
        }
        Ultralight.lib.ultralightui_free(buffer)
    }
}
