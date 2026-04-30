plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":api"))
    api(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)
}
