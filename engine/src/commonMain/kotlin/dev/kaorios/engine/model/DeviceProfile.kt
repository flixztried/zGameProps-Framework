package dev.kaorios.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceProfile(
    val name: String,
    val packages: List<String>,
    val brand: String,
    val device: String,
    val manufacturer: String,
    val model: String
)
