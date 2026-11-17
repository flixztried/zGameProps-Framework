package dev.kaorios.engine.patch

import dev.kaorios.engine.model.DeviceProfile
import dev.kaorios.engine.model.PatchResult

interface VersionPatcher {
    val apiLevel: Int
    fun patch(
        stockJarPath: String,
        outputJarPath: String,
        workspaceDir: String,
        profiles: List<DeviceProfile>
    ): PatchResult
}
