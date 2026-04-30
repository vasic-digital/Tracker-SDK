plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "lava.sdk"
    version = "0.1.0-SNAPSHOT"
}
