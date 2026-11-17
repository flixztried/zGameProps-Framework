package dev.kaorios.engine.model

enum class PatchTarget {
    FRAMEWORK_JAR,
    SERVICES_JAR
}

data class PatchResult(
    val success: Boolean,
    val message: String,
    val patchedJarPath: String? = null
)
