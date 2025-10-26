package ultralightui

interface UltralightNative {
    fun ultralightui_init()
    fun ultralightui_exit()
    fun ultralightui_open_window(url: String)
    fun ultralightui_create_view(url: String, width: Int, height: Int, transparent: Int): Int
    fun ultralightui_view_set_size(view: Int, width: Int, height: Int)
    fun ultralightui_remove_view(view: Int)
    fun ultralightui_copy_from_view(view: Int, buf: Long, len: Long)
    fun ultralightui_report_mouse_move(view: Int, x: Int, y: Int)
    fun ultralightui_report_mouse_down(view: Int, x: Int, y: Int, button: Int)
    fun ultralightui_report_mouse_up(view: Int, x: Int, y: Int, button: Int)
    fun ultralightui_report_scroll(view: Int, x: Int, y: Int)
    fun ultralightui_report_focus(view: Int, focus: Int)
    fun ultralightui_report_key_down(view: Int, scancode: Int, key_mods: Int)
    fun ultralightui_report_key_up(view: Int, scancode: Int, key_mods: Int)
    fun ultralightui_report_input(view: Int, text: String)
    fun ultralightui_alloc(size: Long): Long
    fun ultralightui_free(ptr: Long)
    fun ultralightui_save_to_png(path: String, buf: Long, width: Int, height: Int): Int
}
