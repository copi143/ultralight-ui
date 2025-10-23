package ultralightui

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL21

class UltralightView {
    private var _id: Int = -1
    private var _width: Int = 800
    private var _height: Int = 600
    private var _bufferSize: Long = (_width * _height * 4).toLong()
    private var _buffer: Long = 0
    private var _renderAt: UltralightViewRenderAt = UltralightViewRenderAt.None

    val id: Int
        get() = _id
    var x: Int = 0
    var y: Int = 0
    var width: Int
        get() = _width
        set(value) {
            setSize(value, _height)
        }
    var height: Int
        get() = _height
        set(value) {
            setSize(_width, value)
        }
    val buffer: Long
        get() = _buffer
    val bufferSize: Long
        get() = _bufferSize
    var textureWidth: Int = 0
    var textureHeight: Int = 0
    var textureId: Int = 0
    val textureNeedsResize: Boolean
        get() = textureWidth != width || textureHeight != height
    var renderAt: UltralightViewRenderAt
        get() = _renderAt
        set(value) {
            _renderAt = value
        }

    constructor(url: String, transparent: Int = 0) {
        _id = Ultralight.lib.ultralightui_create_view(url, width, height, transparent)
        if (_id < 0) {
            throw Exception("Failed to create Ultralight view")
        }
        _buffer = Ultralight.alloc(_bufferSize)
        if (buffer == 0L) {
            throw Exception("Failed to allocate Ultralight buffer")
        }
    }

    fun free() {
        Ultralight.lib.ultralightui_remove_view(_id)
        _id = -1
        Ultralight.free(buffer)
        _buffer = 0
        _bufferSize = 0
    }

    fun updateBuffer(): Long {
        Ultralight.lib.ultralightui_copy_from_view(_id, buffer, bufferSize)
        return buffer
    }

    private fun glTexImage2D() {
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL21.GL_SRGB8_ALPHA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer
        )
    }

    private fun glTexSubImage2D() {
        GL11.glTexSubImage2D(
            GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer
        )
    }

    fun uploadTexture() {
        if (textureId == 0) {
            textureId = GL11.glGenTextures()
        }
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4)
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, width)
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0)
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0)
        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0)
        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        updateBuffer()
        if (textureNeedsResize) {
            glTexImage2D()
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            textureWidth = width
            textureHeight = height
        } else {
            glTexSubImage2D()
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    fun moveTo(x: Int, y: Int) {
        if (x == this.x && y == this.y) {
            return
        }
        this.x = x
        this.y = y
    }

    fun setSize(width: Int, height: Int) {
        if (width == _width && height == _height) {
            return
        }
        Ultralight.lib.ultralightui_view_set_size(_id, width, height)
        _width = width
        _height = height
        val newBufferSize = (_width * _height * 4).toLong()
        if (newBufferSize != _bufferSize) {
            Ultralight.free(_buffer)
            _buffer = Ultralight.alloc(newBufferSize)
            if (_buffer == 0L) {
                throw Exception("Failed to allocate Ultralight buffer")
            }
            _bufferSize = newBufferSize
        }
    }

    fun clickTest(px: Int, py: Int): UltralightViewClickAt {
        val bw = Ultralight.windowBorderWidth
        return if (px < x - bw) {
            UltralightViewClickAt.Outside
        } else if (px < x) {
            if (py < y - bw) {
                UltralightViewClickAt.Outside
            } else if (py < y) {
                UltralightViewClickAt.TopLeftCorner
            } else if (py < y + height) {
                UltralightViewClickAt.LeftEdge
            } else if (py < y + height + bw) {
                UltralightViewClickAt.BottomLeftCorner
            } else {
                UltralightViewClickAt.Outside
            }
        } else if (px < x + width) {
            if (py < y - bw) {
                UltralightViewClickAt.Outside
            } else if (py < y) {
                UltralightViewClickAt.TopEdge
            } else if (py < y + height) {
                UltralightViewClickAt.Inside
            } else if (py < y + height + bw) {
                UltralightViewClickAt.BottomEdge
            } else {
                UltralightViewClickAt.Outside
            }
        } else if (px < x + width + bw) {
            if (py < y - bw) {
                UltralightViewClickAt.Outside
            } else if (py < y) {
                UltralightViewClickAt.TopRightCorner
            } else if (py < y + height) {
                UltralightViewClickAt.RightEdge
            } else if (py < y + height + bw) {
                UltralightViewClickAt.BottomRightCorner
            } else {
                UltralightViewClickAt.Outside
            }
        } else {
            UltralightViewClickAt.Outside
        }
    }

    fun reportMouseMove(x: Int, y: Int) {
        Ultralight.lib.ultralightui_report_mouse_move(_id, x - this.x, y - this.y)
    }

    fun reportMouseDown(x: Int, y: Int, button: Int) {
        Ultralight.lib.ultralightui_report_mouse_down(_id, x - this.x, y - this.y, button)
    }

    fun reportMouseUp(x: Int, y: Int, button: Int) {
        Ultralight.lib.ultralightui_report_mouse_up(_id, x - this.x, y - this.y, button)
    }

    fun reportScroll(dx: Int, dy: Int) {
        Ultralight.lib.ultralightui_report_scroll(_id, dx, dy)
    }
}
