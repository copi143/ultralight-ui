package ultralightui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.GameRenderer
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

fun drawTexture(textureId: Int, x: Double, y: Double, w: Double, h: Double) {
    RenderSystem.resetTextureMatrix()

    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader(GameRenderer::getPositionTexShader)
    RenderSystem.setShaderTexture(0, textureId)
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

    val buffer = Tesselator.getInstance().builder
    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
    buffer.vertex(x, y, 0.0).uv(0.0f, 0.0f).endVertex()
    buffer.vertex(x, y + h, 0.0).uv(0.0f, 1.0f).endVertex()
    buffer.vertex(x + w, y + h, 0.0).uv(1.0f, 1.0f).endVertex()
    buffer.vertex(x + w, y, 0.0).uv(1.0f, 0.0f).endVertex()
    BufferUploader.drawWithShader(buffer.end())

    RenderSystem.disableBlend()
}
