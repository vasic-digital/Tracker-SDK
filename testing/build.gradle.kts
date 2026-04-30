plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":api"))
    api(project(":mirror"))
    api(project(":registry"))
    api(libs.kotlinx.coroutines.core)
    // intentionally api: consumers use JUnit 4 assertions in tests built on this module
    api(libs.junit)
    implementation(libs.truth)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
}
