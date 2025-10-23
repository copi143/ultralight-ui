//
// package ultralightui
//
// import net.janrupf.ujr.api.UltralightPlatform
// import net.janrupf.ujr.api.UltralightRenderer
// import net.janrupf.ujr.api.UltralightView
// import net.janrupf.ujr.api.bitmap.UltralightBitmap
// import net.janrupf.ujr.api.bitmap.UltralightBitmapSurface
// import net.janrupf.ujr.api.math.IntRect
// import com.mojang.blaze3d.platform.GlStateManager
// import net.minecraft.client.Minecraft
// import org.lwjgl.opengl.GL11
// import org.lwjgl.opengl.GL12
// import java.nio.ByteBuffer
//
// /**
//  * Class used for controlling the WebGUI rendered on top of the OpenGL GUI.
//  */
// class ViewController(renderer: UltralightRenderer, view: UltralightView) {
//     private val platform: UltralightPlatform?
//     private val renderer: UltralightRenderer
//     private val view: UltralightView
//
//     private var glTexture: Int
//     private var lastJavascriptGarbageCollections: Long
//
//     /**
//      * Constructs a new [ViewController] and retrieves the platform.
//      */
//     init {
//         this.platform = UltralightPlatform.instance()
//
//         this.renderer = renderer
//
//
//         this.view = view
//
//         this.glTexture = -1
//         this.lastJavascriptGarbageCollections = 0
//     }
//
//     /**
//      * Loads the specified URL into this controller.
//      *
//      * @param url The URL to load
//      */
//     fun loadURL(url: String?) {
//         this.view.loadURL(url)
//     }
//
//     /**
//      * Updates and renders the renderer
//      */
//     fun update() {
//         this.renderer.update()
//         this.renderer.render()
//
//         if (lastJavascriptGarbageCollections == 0L) {
//             lastJavascriptGarbageCollections = System.currentTimeMillis()
//         } else if (System.currentTimeMillis() - lastJavascriptGarbageCollections > 1000) {
//             println("Garbage collecting Javascript...")
//             this.view.lockJavascriptContext().use { lock ->
//                 lock.getContext().garbageCollect()
//             }
//             lastJavascriptGarbageCollections = System.currentTimeMillis()
//         }
//     }
//
//     /**
//      * Resizes the web view.
//      *
//      * @param width  The new view width
//      * @param height The new view height
//      */
//     fun resize(width: Int, height: Int) {
//         this.view.resize(width, height)
//     }
//
//     /**
//      * Render the current image using OpenGL
//      */
//     fun render() {
//         if (glTexture == -1) {
//             createGLTexture()
//         }
//
//         val surface: UltralightBitmapSurface = this.view.surface() as UltralightBitmapSurface
//         val bitmap: UltralightBitmap = surface.bitmap()
//
//         val width = view.width() as Int
//         val height = view.height() as Int
//
//         // Prepare OpenGL for 2D textures and bind our texture
//         GL11.glEnable(GL11.GL_TEXTURE_2D)
//
//         GlStateManager.bindTexture(this.glTexture)
//
//         val dirtyBounds: IntRect = surface.dirtyBounds()
//
//         if (dirtyBounds.isValid()) {
//             val imageData: ByteBuffer = bitmap.lockPixels()
//             GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, bitmap.rowBytes() as Int / 4)
//             if (dirtyBounds.width() === width && dirtyBounds.height() === height) { // Update full image
//                 GL11.glTexImage2D(
//                     GL11.GL_TEXTURE_2D,
//                     0,
//                     GL11.GL_RGBA8,
//                     width,
//                     height,
//                     0,
//                     GL12.GL_BGRA,
//                     GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
//                     imageData
//                 )
//                 GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0)
//             } else { // Update partial image
//                 val x: Int = dirtyBounds.x()
//                 val y: Int = dirtyBounds.y()
//                 val dirtyWidth: Int = dirtyBounds.width()
//                 val dirtyHeight: Int = dirtyBounds.height()
//                 val startOffset = ((y * bitmap.rowBytes()) + x * 4) as Int
//
//                 GL11.glTexSubImage2D(
//                     GL11.GL_TEXTURE_2D,
//                     0,
//                     x,
//                     y,
//                     dirtyWidth,
//                     dirtyHeight,
//                     GL12.GL_BGRA,
//                     GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
//                     imageData.position(startOffset) as ByteBuffer
//                 )
//             }
//             GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0)
//
//             bitmap.unlockPixels()
//             surface.clearDirtyBounds()
//         }
//
//         // Set up the OpenGL state for rendering of a fullscreen quad
//         GL11.glPushAttrib(GL11.GL_ENABLE_BIT or GL11.GL_COLOR_BUFFER_BIT or GL11.GL_TRANSFORM_BIT)
//         GL11.glMatrixMode(GL11.GL_PROJECTION)
//         GL11.glPushMatrix()
//         GL11.glLoadIdentity()
//         GL11.glOrtho(0.0, this.view.width(), this.view.height(), 0.0, -1.0, 1.0)
//         GL11.glMatrixMode(GL11.GL_MODELVIEW)
//         GL11.glPushMatrix()
//
//         // Disable lighting and scissoring, they could mess up th renderer
//         GL11.glLoadIdentity()
//         GL11.glDisable(GL11.GL_LIGHTING)
//         GL11.glDisable(GL11.GL_SCISSOR_TEST)
//         GL11.glEnable(GL11.GL_BLEND)
//         GL11.glEnable(GL11.GL_TEXTURE_2D)
//         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
//
//         // Make sure we draw with a neutral color
//         // (so we don't mess with the color channels of the image)
//         GL11.glColor4f(1f, 1f, 1f, 1f)
//
//         GL11.glBegin(GL11.GL_QUADS)
//
//         // Lower left corner, 0/0 on the screen space, and 0/0 of the image UV
//         GL11.glTexCoord2f(0f, 0f)
//         GL11.glVertex2f(0f, 0f)
//
//         // Upper left corner
//         GL11.glTexCoord2f(0f, 1f)
//         GL11.glVertex2i(0, height)
//
//         // Upper right corner
//         GL11.glTexCoord2f(1f, 1f)
//         GL11.glVertex2i(width, height)
//
//         // Lower right corner
//         GL11.glTexCoord2f(1f, 0f)
//         GL11.glVertex2i(width, 0)
//
//         GL11.glEnd()
//
//         GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
//
//         // Restore OpenGL state
//         GL11.glPopMatrix()
//         GL11.glMatrixMode(GL11.GL_PROJECTION)
//         GL11.glPopMatrix()
//         GL11.glMatrixMode(GL11.GL_MODELVIEW)
//
//         GL11.glDisable(GL11.GL_TEXTURE_2D)
//         GL11.glPopAttrib()
//     }
//
//     fun getView(): UltralightView {
//         return view
//     }
//
//     /**
//      * Sets up the OpenGL texture for rendering
//      */
//     private fun createGLTexture() {
//         GL11.glEnable(GL11.GL_TEXTURE_2D)
//         this.glTexture = GL11.glGenTextures()
//         GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.glTexture)
//
//         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST.toFloat())
//         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST.toFloat())
//         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE.toFloat())
//         GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE.toFloat())
//         GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
//         GL11.glDisable(GL11.GL_TEXTURE_2D)
//     }
//
//     fun onMouseClick(x: Int, y: Int, mouseButton: Int, buttonDown: Boolean) {
//         val event: UltralightMouseEvent = UltralightMouseEvent()
//         val button: UltralightMouseEventButton?
//         when (mouseButton) {
//             0 -> button = UltralightMouseEventButton.LEFT
//             1 -> button = UltralightMouseEventButton.RIGHT
//             3 -> button = UltralightMouseEventButton.MIDDLE
//             else -> button = UltralightMouseEventButton.MIDDLE
//         }
//         val scaledResolution: ScaledResolution = ScaledResolution(Minecraft.getMinecraft())
//         event.button(button)
//         event.x(x * scaledResolution.getScaleFactor())
//         event.y(y * scaledResolution.getScaleFactor())
//         event.type(if (buttonDown) UltralightMouseEventType.DOWN else UltralightMouseEventType.UP)
//
//         view.fireMouseEvent(event)
//     }
//
//     fun onMouseMove(x: Int, y: Int) {
//         val scaledResolution: ScaledResolution = ScaledResolution(Minecraft.getMinecraft())
//         val event: UltralightMouseEvent = UltralightMouseEvent()
//         event.x(x * scaledResolution.getScaleFactor())
//         event.y(y * scaledResolution.getScaleFactor())
//         event.type(UltralightMouseEventType.MOVED)
//         view.fireMouseEvent(event)
//     }
//
//     fun onKeyDown(c: Char, key: Int) {
//         val event: UltralightKeyEvent = UltralightKeyEvent()
//         event.virtualKeyCode(UltralightKeyMapper.getKey(key))
//         event.unmodifiedText(c.toString())
//
//         val keyType: UltralightKeyMapper.KeyType? = UltralightKeyMapper.getKeyType(key)
//
//         if (keyType === UltralightKeyMapper.KeyType.ACTION) {
//             event.type(UltralightKeyEventType.RAW_DOWN)
//         } else if (keyType === UltralightKeyMapper.KeyType.CHAR) {
//             event.type(UltralightKeyEventType.CHAR)
//         }
//
//
//         event.text(c.toString())
//         event.keyIdentifier(UltralightKeyEvent.getKeyIdentifierFromVirtualKeyCode(UltralightKeyMapper.getKey(key)))
//
//         view.fireKeyEvent(event)
//     }
// }
