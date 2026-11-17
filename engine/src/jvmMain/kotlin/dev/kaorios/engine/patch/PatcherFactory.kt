package dev.kaorios.engine.patch

object PatcherFactory {
    fun forApi(api: Int): VersionPatcher? = when (api) {
        30, 31, 32 -> Android11Patcher(api)
        33 -> Android13Patcher(api)
        34 -> Android14Patcher(api)
        35 -> Android15Patcher(api)
        36 -> Android16Patcher(api)
        37 -> Android17Patcher(api)
        else -> null
    }
}
