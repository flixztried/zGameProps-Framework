plugins {
    kotlin("multiplatform") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    jvm {
        withJava()
        testRuns["test"].executionTask.configure { useJUnitPlatform() }
    }
    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "17" } }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.android.tools.smali:smali:3.0.9")
                implementation("com.android.tools.smali:smali-baksmali:3.0.9")
                implementation("com.android.tools.smali:smali-dexlib2:3.0.9")
                implementation("com.android.tools.smali:smali-util:3.0.9")
                implementation("com.google.guava:guava:31.1-jre")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
            }
        }
    }
}
