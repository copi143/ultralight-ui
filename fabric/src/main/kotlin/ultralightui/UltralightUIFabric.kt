package ultralightui

import ultralightui.platform.setupKeyBinding

fun init() {
    Constants.LOG.info("Hello Fabric world from Kotlin!")
    CommonObject.init()
    Ultralight.init()
}

fun serverInit() {
    Ultralight.serverInit()
}

fun clientInit() {
    Ultralight.clientInit()
    setupKeyBinding()
}
