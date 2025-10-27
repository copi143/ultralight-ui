package ultralightui

import jnr.ffi.LibraryLoader
import net.minecraft.client.Minecraft
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object Ultralight {
    lateinit var lib: UltralightNative

    const val windowBorderWidth: Int = 8
    const val windowBorderColor: Int = 0x7738bcce

    var hideAllViews: Boolean = false

    var views: MutableMap<Int, UltralightView> = mutableMapOf()

    var fullScreenView: UltralightView? = null

    private var _lastFocusedView: Int? = null
    var lastFocusedView: UltralightView?
        get() = _lastFocusedView?.let { views[it] }
        set(value) {
            _lastFocusedView = value?.id
        }

    var showFakeHotbar: Boolean = false // 展示一个用于调整 UI 设定的假快捷栏

    val lib_soname: String by lazy {
        val os = System.getProperty("os.name")
        if (os.startsWith("Linux")) {
            "ultralight/libultralightui.so"
        } else if (os.startsWith("Windows")) {
            "ultralight\\ultralightui.dll"
        } else if (os.startsWith("Mac")) {
            throw Exception("MacOS is not supported yet.")
        } else {
            throw Exception("Unknown operating system: $os")
        }
    }

    private fun downloadResourcesIfNeeded() {
        val mc = Minecraft.getInstance().gameDirectory.absolutePath
        if (!Paths.get(mc, "ultralight").toFile().exists()) {
            Paths.get(mc, "ultralight").toFile().mkdirs()
        } // 之后支持自动下载
        if (!Paths.get(mc, lib_soname).toFile().exists()) {
            throw Exception("Ultralight native library not found at $mc/$lib_soname.")
        }
    }

    var initialized: Boolean = false
    fun init() {
        if (initialized) {
            Constants.LOG.error("Ultralight.init called more than once!")
            return
        }
        initialized = true
    }

    var clientInitialized: Boolean = false
    fun clientInit() {
        if (clientInitialized) {
            Constants.LOG.error("Ultralight.clientInit called more than once!")
            return
        }
        clientInitialized = true

        downloadResourcesIfNeeded()
        val mc = Minecraft.getInstance().gameDirectory.absolutePath
        val path = Paths.get(mc, lib_soname).absolutePathString()
        lib = LibraryLoader.create(UltralightNative::class.java).load(path)
        lib.ultralightui_init()
        val view = createView("https://cytoscape.org/cytoscape.js-klay/")
        view.renderAt = UltralightViewRenderAt.AboveWorld
    }

    var serverInitialized: Boolean = false
    fun serverInit() {
        if (serverInitialized) {
            Constants.LOG.error("Ultralight.serverInit called more than once!")
            return
        }
        serverInitialized = true
    }

    fun createView(url: String, transparent: Int = 0): UltralightView {
        val view = UltralightView(url, transparent)
        views[view.id] = view
        return view
    }

    fun deleteView(id: Int) {
        views[id]?.free()
        views.remove(id)
    }

    fun alloc(size: Long): Long {
        return lib.ultralightui_alloc(size)
    }

    fun free(ptr: Long) {
        lib.ultralightui_free(ptr)
    }
}
