package dev.kaorios.engine.patch

import dev.kaorios.engine.model.DeviceProfile
import java.io.File

open class Android13Patcher(api: Int) : BaseSmaliPatcher(api) {
    override fun injectProps(smaliRoot: File, profiles: List<DeviceProfile>): Boolean {
        val instrumentation = smaliRoot.walkTopDown().firstOrNull { it.isFile && it.path.endsWith("android/app/Instrumentation.smali") } ?: return false
        var text = instrumentation.readText()
        
        val methodRegex = Regex("""\.method public newApplication\(Ljava/lang/Class;Landroid/content/Context;\)Landroid/app/Application;.*?\.end method""", RegexOption.DOT_MATCHES_ALL)
        val match = methodRegex.find(text) ?: return false
        val original = match.value
        
        // Find the last return-object
        val returnIdx = original.lastIndexOf("return-object")
        if (returnIdx == -1) return false
        
        // Count .param directives to calculate the correct `p` register
        // For instance methods, p0 is `this`, so p<paramCount> is the last parameter (Context)
        val paramCount = Regex("""\.param p\d+""").findAll(original).count()
        val isStatic = original.contains(".method public static")
        val contextReg = if (isStatic) paramCount - 1 else paramCount
        
        val injected = original.substring(0, returnIdx) + "    invoke-static {p$contextReg}, Lio/mesalabs/unica/GamesPropsUtils;->setProps(Landroid/content/Context;)V\n\n" + original.substring(returnIdx)
        instrumentation.writeText(text.replace(original, injected))
        return true
    }
}
