package ultralightui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ultralightui.*;

import java.util.Map;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Final
    @Shadow
    Minecraft minecraft;

    @Unique
    boolean ultralight_ui$afterWorldBeforeUI$aboveWorldFirstCall = false;

    @Unique
    boolean ultralight_ui$afterWorldBeforeUI$aboveHudFirstCall = false;

    @Unique
    boolean ultralight_ui$afterWorldBeforeUI$aboveGuiFirstCall = false;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void beforeRender(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            throw new IllegalStateException("Ultralight UI rendering must be performed on the Render Thread!");
        }
        if (minecraft.noRender) return;
        if (minecraft.screen instanceof UltralightBrowserScreen) return;
        ultralight_ui$afterWorldBeforeUI$aboveWorldFirstCall = true;
        ultralight_ui$afterWorldBeforeUI$aboveHudFirstCall = true;
        ultralight_ui$afterWorldBeforeUI$aboveGuiFirstCall = true;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;setOrtho(FFFFFF)Lorg/joml/Matrix4f;", remap = false))
    //    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"))
    //    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V", shift = At.Shift.AFTER))
    private void aboveWorld(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        if (minecraft.noRender) return;
        if (ultralight_ui$afterWorldBeforeUI$aboveWorldFirstCall) {
            ultralight_ui$afterWorldBeforeUI$aboveWorldFirstCall = false;
            ultralight_ui$drawViews(UltralightViewRenderAt.AboveWorld);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
    private void AboveGui(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        if (minecraft.noRender) return;
        if (ultralight_ui$afterWorldBeforeUI$aboveGuiFirstCall) {
            ultralight_ui$afterWorldBeforeUI$aboveGuiFirstCall = false;
            ultralight_ui$drawViews(UltralightViewRenderAt.AboveGui);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V", shift = At.Shift.AFTER))
    private void FullScreenView(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        if (minecraft.noRender) return;
        if (Ultralight.INSTANCE.getFullScreenView() != null) {
            ultralight_ui$drawView(Ultralight.INSTANCE.getFullScreenView());
        }
    }

    @Unique
    private void ultralight_ui$drawView(UltralightView v) {
        v.uploadTexture();

        int screenWidth = minecraft.getWindow().getWidth();
        int screenHeight = minecraft.getWindow().getHeight();
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        RenderSystem.viewport(0, 0, screenWidth, screenHeight);
        RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, screenWidth, screenHeight, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.getModelViewStack().translate(0.0F, 0.0F, -1100.0F);
        RenderSystem.applyModelViewMatrix();

        ultralight_ui$drawTexture(v.getTextureId(), v.getX(), v.getY(), v.getWidth(), v.getHeight());

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, scaledWidth, scaledHeight, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
    }

    @Unique
    private void ultralight_ui$drawViews(UltralightViewRenderAt at) {
        Map<Integer, UltralightView> view = Ultralight.INSTANCE.getViews();
        for (UltralightView v : view.values()) {
            if (v.getRenderAt() == at) {
                v.uploadTexture();
            }
        }

        int screenWidth = minecraft.getWindow().getWidth();
        int screenHeight = minecraft.getWindow().getHeight();
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        RenderSystem.viewport(0, 0, screenWidth, screenHeight);
        RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, screenWidth, screenHeight, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.getModelViewStack().translate(0.0F, 0.0F, -1100.0F);
        RenderSystem.applyModelViewMatrix();

        for (UltralightView v : view.values()) {
            if (v.getRenderAt() == at) {
                if (minecraft.screen instanceof UltralightScreen) {
                    int width = Ultralight.windowBorderWidth;
                    int color = Ultralight.windowBorderColor;
                    ultralight_ui$drawBackground(color, v.getX() - width, v.getY() - width, v.getWidth() + 2 * width, v.getHeight() + 2 * width);
                    ultralight_ui$drawBackground(0xffffff, v.getX(), v.getY(), v.getWidth(), v.getHeight());
                }
                if (minecraft.screen instanceof TemporaryEscapeScreen) {
                    ultralight_ui$drawBackground(0xffffff, v.getX(), v.getY(), v.getWidth(), v.getHeight());
                }
                ultralight_ui$drawTexture(v.getTextureId(), v.getX(), v.getY(), v.getWidth(), v.getHeight());
            }
        }

        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, scaledWidth, scaledHeight, 0.0F, 1000.0F, 21000.0F), VertexSorting.ORTHOGRAPHIC_Z);
    }

//    @Unique
//    private static void ultralight_ui$uploadTexture(UltralightView view) {
//        if (view.getTextureId() == 0) {
//            view.setTextureId(GL11.glGenTextures());
//        }
//        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
//        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, view.getWidth());
//        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
//        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
//        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0);
//        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, view.getTextureId());
//        int width = view.getWidth();
//        int height = view.getHeight();
//        long buffer = view.updateBuffer();
//        if (view.getTextureNeedsResize()) {
//            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL21.GL_SRGB8_ALPHA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//            view.setTextureWidth(width);
//            view.setTextureHeight(height);
//        } else {
//            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//        }
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//    }

    @Unique
    private static void ultralight_ui$drawTexture(int textureId, float x, float y, float w, float h) {
        RenderSystem.resetTextureMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y, 0).uv(0, 0).endVertex();
        buffer.vertex(x, y + h, 0).uv(0, 1).endVertex();
        buffer.vertex(x + w, y + h, 0).uv(1, 1).endVertex();
        buffer.vertex(x + w, y, 0).uv(1, 0).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    @Unique
    private static float ultralight_ui$srgbToLinear(float c) {
        if (c <= 0.04045f) {
            return c / 12.92f;
        } else {
            return (float) Math.pow((c + 0.055f) / 1.055f, 2.4f);
        }
    }

    @Unique
    private static void ultralight_ui$drawBackground(int color, float x, float y, float w, float h) {
        float r = ultralight_ui$srgbToLinear((float) (color >>> 16 & 0xFF) / 255f);
        float g = ultralight_ui$srgbToLinear((float) (color >>> 8 & 0xFF) / 255f);
        float b = ultralight_ui$srgbToLinear((float) (color & 0xFF) / 255f);
        float a = (float) (color >>> 24 & 0xFF) / 255f;
        if (a == 0) a = 1;

        RenderSystem.resetTextureMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, a);

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(x, y, 1).endVertex();
        buffer.vertex(x, y + h, 1).endVertex();
        buffer.vertex(x + w, y + h, 1).endVertex();
        buffer.vertex(x + w, y, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }
}
