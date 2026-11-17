package dev.kaorios.engine.patch

import dev.kaorios.engine.model.DeviceProfile
import java.io.File

class Android15Patcher(api: Int) : Android13Patcher(api) {
    override fun preProcess(smaliRoot: File): Boolean {
        return try {
            smaliRoot.walkTopDown().filter { it.isFile && it.extension == "smali" }.forEach { file ->
                var text = file.readText()
                text = stubMethod(text, "equals", false)
                text = stubMethod(text, "hashCode", false)
                text = stubMethod(text, "toString", true)
                file.writeText(text)
            }
            true
        } catch (t: Throwable) { false }
    }

    private fun stubMethod(source: String, methodName: String, returnsObject: Boolean): String {
        val regex = Regex("""\\.method .*?$methodName.*?\\n(?:.*?\\n)*?\\.end method""", RegexOption.DOT_MATCHES_ALL)
        return regex.replace(source) { match ->
            val body = match.value
            if (!body.contains("invoke-custom")) return@replace body
            if (returnsObject) ".method public $methodName()Ljava/lang/String;\\n    .registers 1\\n    const/4 v0, 0x0\\n    return-object v0\\n.end method"
            else ".method public $methodName()I\\n    .registers 2\\n    const/4 v0, 0x0\\n    return v0\\n.end method"
        }
    }
}
