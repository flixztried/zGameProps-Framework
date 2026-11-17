package dev.kaorios.engine.patch

import dev.kaorios.engine.dex.SmaliRoundTrip
import dev.kaorios.engine.model.DeviceProfile
import dev.kaorios.engine.model.PatchResult
import java.io.File

abstract class BaseSmaliPatcher(val apiLevel: Int) : VersionPatcher {
    protected val targetClass = "Landroid/app/Instrumentation;"

    protected open fun preProcess(smaliRoot: File): Boolean = true
    protected abstract fun injectProps(smaliRoot: File, profiles: List<DeviceProfile>): Boolean

    override fun patch(stockJarPath: String, outputJarPath: String, workspaceDir: String, profiles: List<DeviceProfile>): PatchResult {
        val workspace = File(workspaceDir).apply { mkdirs() }
        val (dexEntry, probeDex) = SmaliRoundTrip.extractDexContaining(stockJarPath, targetClass, apiLevel)
            ?: return PatchResult(false, "Instrumentation class not found")

        val smaliRoot = File(workspace, "smali").apply { mkdirs() }
        if (!SmaliRoundTrip.disassemble(probeDex, smaliRoot, apiLevel)) return PatchResult(false, "Disassembly failed")
        if (!preProcess(smaliRoot)) return PatchResult(false, "Pre-processing failed")
        if (!injectProps(smaliRoot, profiles)) return PatchResult(false, "Injection failed")

        val patchedDex = File(workspace, "patched.dex")
        if (!SmaliRoundTrip.assemble(smaliRoot, patchedDex, apiLevel)) return PatchResult(false, "Assembly failed")
        if (!SmaliRoundTrip.rebuildJar(File(stockJarPath), patchedDex, dexEntry, File(outputJarPath))) return PatchResult(false, "JAR rebuild failed")

        return PatchResult(true, "Patched successfully", outputJarPath)
    }
}
