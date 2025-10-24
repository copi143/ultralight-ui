package ultralightui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ultralightui.UltralightViewClickAt.Inside
import ultralightui.UltralightViewClickAt.Outside

class TemporaryEscapeScreen : Screen(Component.literal("Ultralight UI")) {
    private var mouseLeftButtonDown = false
    private var mouseMiddleButtonDown = false
    private var mouseRightButtonDown = false

    private var focusedView: Int? = null
    private var mouseClickedView: Int? = null

    private var scrolledDelta: Double = 0.0

    private val pendingMouseEvent: MutableList<Triple<Int, Int, Int>> = mutableListOf()

    override fun isPauseScreen(): Boolean = false

    override fun init() {
        focusedView = Ultralight.lastFocusedView?.id
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val mouseX = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val mouseY = (mouseY * this.minecraft!!.window.guiScale).toInt()
        for ((_, view) in Ultralight.views) {
            if (mouseClickedView == view.id) {
                for ((x, y, button) in pendingMouseEvent) {
                    when (button) {
                        0 -> {
                            view.reportMouseMove(x, y)
                        }

                        1, 2, 3 -> {
                            view.reportMouseDown(x, y, button - 1)
                        }

                        -1, -2, -3 -> {
                            view.reportMouseUp(x, y, -1 - button)
                        }
                    }
                }
                if (scrolledDelta != 0.0) {
                    view.reportScroll(0, (scrolledDelta * 64).toInt())
                }
                if (!mouseLeftButtonDown && !mouseMiddleButtonDown && !mouseRightButtonDown) {
                    mouseClickedView = null
                    if (view.clickTest(mouseX, mouseY) == Outside) {
                        focusedView?.let { Ultralight.lib.ultralightui_report_focus(it, 0) }
                        focusedView = null
                    }
                }
            } else when (view.clickTest(mouseX, mouseY)) {
                Outside -> continue
                Inside -> {
                    for ((x, y, button) in pendingMouseEvent) {
                        when (button) {
                            0 -> {
                                view.reportMouseMove(x, y)
                            }

                            1, 2, 3 -> {
                                view.reportMouseDown(x, y, button - 1)
                            }

                            -1, -2, -3 -> {
                                view.reportMouseUp(x, y, -1 - button)
                            }
                        }
                    }
                    if (scrolledDelta != 0.0) {
                        view.reportScroll(0, (scrolledDelta * 64).toInt())
                    }
                    if (mouseLeftButtonDown || mouseMiddleButtonDown || mouseRightButtonDown) {
                        mouseClickedView = view.id
                        focusedView = view.id
                        Ultralight.lib.ultralightui_report_focus(view.id, 1)
                    }
                }

                else -> continue
            }
            break
        }
        pendingMouseEvent.clear()
        scrolledDelta = 0.0
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        val mouseX = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val mouseY = (mouseY * this.minecraft!!.window.guiScale).toInt()
        pendingMouseEvent.add(Triple(mouseX, mouseY, 0))
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        when (button) {
            0 -> mouseLeftButtonDown = true
            1 -> mouseRightButtonDown = true
            2 -> mouseMiddleButtonDown = true
        }
        val button = when (button) {
            0 -> 1
            1 -> 3
            2 -> 2
            else -> 0
        }
        val mouseX = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val mouseY = (mouseY * this.minecraft!!.window.guiScale).toInt()
        pendingMouseEvent.add(Triple(mouseX, mouseY, button))
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        when (button) {
            0 -> mouseLeftButtonDown = false
            1 -> mouseRightButtonDown = false
            2 -> mouseMiddleButtonDown = false
        }
        val button = when (button) {
            0 -> -1
            1 -> -3
            2 -> -2
            else -> 0
        }
        val mouseX = (mouseX * this.minecraft!!.window.guiScale).toInt()
        val mouseY = (mouseY * this.minecraft!!.window.guiScale).toInt()
        pendingMouseEvent.add(Triple(mouseX, mouseY, button))
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        scrolledDelta += delta
        return true
    }
}
