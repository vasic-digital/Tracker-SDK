plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":api"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
}
