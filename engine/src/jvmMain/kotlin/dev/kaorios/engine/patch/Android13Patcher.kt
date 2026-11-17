package dev.kaorios.engine.patch

import dev.kaorios.engine.model.DeviceProfile
import java.io.File

open class Android13Patcher(api: Int) : BaseSmaliPatcher(api) {
    override fun injectProps(smaliRoot: File, profiles: List<DeviceProfile>): Boolean {
        val instrumentation = smaliRoot.walkTopDown().firstOrNull { it.isFile && it.path.endsWith("android/app/Instrumentation.smali") } ?: return false
        var text = instrumentation.readText()
        
        val methodRegex = Regex("""\\.method public newApplication\\(Ljava/lang/Class;Landroid/content/Context;\\)Landroid/app/Application;.*?\\.end method""", RegexOption.DOT_MATCHES_ALL)
        val match = methodRegex.find(text) ?: return false
        val original = match.value
        val returnIdx = original.lastIndexOf("return-object p0")
        if (returnIdx == -1) return false
        
        val registers = Regex(""".registers (\\d+)""").find(original)?.groupValues?.get(1)?.toIntOrNull() ?: return false
        val contextReg = registers - 1
        
        val injected = original.substring(0, returnIdx) + "    invoke-static {v$contextReg}, Lio/mesalabs/unica/GamesPropsUtils;->setProps(Landroid/content/Context;)V\\n\\n" + original.substring(returnIdx)
        instrumentation.writeText(text.replace(original, injected))
        return true
    }
}
