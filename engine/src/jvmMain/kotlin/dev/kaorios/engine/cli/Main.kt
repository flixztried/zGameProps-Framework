package dev.kaorios.engine.cli

import dev.kaorios.engine.model.DeviceProfile
import dev.kaorios.engine.patch.PatcherFactory
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>) {
    if (args.size < 4) {
        println("Usage: Main <input_jar> <output_jar> <api_level> <profiles.json>")
        System.exit(1)
    }
    val stockJar = args[0]
    val outJar = args[1]
    val apiLevel = args[2].toInt()
    val profilesFile = File(args[3])
    
    val patcher = PatcherFactory.forApi(apiLevel) ?: run {
        println("Unsupported API level: $apiLevel")
        System.exit(1)
    }
    
    val profiles = if (profilesFile.exists()) {
        Json.decodeFromString<List<DeviceProfile>>(profilesFile.readText())
    } else emptyList()
    
    val result = patcher.patch(stockJar, outJar, File.createTempDir().absolutePath, profiles)
    if (!result.success) {
        println("Failed: ${result.message}")
        System.exit(1)
    }
    println("Success: ${result.message}")
}
