import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    jvm {
        withJava()
        testRuns["test"].executionTask.configure { useJUnitPlatform() }
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
            resources.srcDir(layout.buildDirectory.dir("generated-payload"))
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
            }
        }
    }
}

val compilePayload by tasks.registering(Copy::class) {
    from(rootProject.file("payload/smali"))
    into(layout.buildDirectory.dir("generated-payload"))
}

// Force a predictable jar name: engine/build/libs/engine.jar
tasks.named<Jar>("jvmJar") {
    dependsOn(compilePayload)
    archiveFileName.set("engine.jar")
}
