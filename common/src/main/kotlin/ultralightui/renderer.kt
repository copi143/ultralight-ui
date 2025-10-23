package ultralightui

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL21

fun uploadTexture(id: Int, buffer: Long, width: Int, height: Int, resized: Boolean) {
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4)
    GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, width)
    GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0)
    GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0)
    GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0)
    GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    if (resized) {
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL21.GL_SRGB8_ALPHA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer
        )
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
    } else {
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
    }
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
}

fun downloadTexture(id: Int, buffer: Long, width: Int, height: Int) {
    GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 4)
    GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, width)
    GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, 0)
    GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, 0)
    GL11.glPixelStorei(GL11.GL_PACK_SWAP_BYTES, 0)
    GL11.glPixelStorei(GL11.GL_PACK_LSB_FIRST, 0)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
}
