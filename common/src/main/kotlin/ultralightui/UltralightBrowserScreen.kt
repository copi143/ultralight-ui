package ultralightui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class UltralightBrowserScreen : Screen {
    private var lastView: UltralightView? = null
    private val view: UltralightView

    constructor(url: String) : super(Component.literal("Ultralight Browser")) {
        view = Ultralight.createView(url)
        view.setSize(Minecraft.getInstance().window.width, Minecraft.getInstance().window.height)
    }

    override fun init() {
        super.init()
        lastView = Ultralight.fullScreenView
        Ultralight.fullScreenView = view
    }

    override fun onClose() {
        Ultralight.fullScreenView = lastView
        lastView = null
        Ultralight.deleteView(view.id)
        super.onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) { // mixin 会处理渲染
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)
        view.setSize(this.minecraft!!.window.width, this.minecraft!!.window.height)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        val x = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val y = (mouseY * this.minecraft!!.window.guiScale).toInt()
        view.reportMouseMove(x, y)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val b = when (button) {
            0 -> 1
            1 -> 3
            2 -> 2
            else -> 0
        }
        val x = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val y = (mouseY * this.minecraft!!.window.guiScale).toInt()
        view.reportMouseDown(x, y, b - 1)
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val b = when (button) {
            0 -> 1
            1 -> 3
            2 -> 2
            else -> 0
        }
        val x = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val y = (mouseY * this.minecraft!!.window.guiScale).toInt()
        view.reportMouseUp(x, y, b - 1)
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        view.reportScroll(0, (delta * 64).toInt())
        return true
    }
}
