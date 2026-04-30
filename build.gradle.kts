import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "lava.sdk"
    version = "0.1.0-SNAPSHOT"
}

// JVM 17 target across every Tracker-SDK subproject. The default toolchain
// would otherwise track the host JDK (typically 21), which produces JVM 21
// class files that downstream consumers (Lava's :app on AGP 8.6.1 / D8 with
// JVM 17 dexing) cannot consume. JVM 17 is the lowest common denominator
// across the consumer set today; bump only if a deliberate consumer migration
// signs off on the breaking change.
subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType(KotlinJvmCompile::class.java).configureEach {
            compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        }
        tasks.withType(JavaCompile::class.java).configureEach {
            sourceCompatibility = JavaVersion.VERSION_17.toString()
            targetCompatibility = JavaVersion.VERSION_17.toString()
        }
    }
}
