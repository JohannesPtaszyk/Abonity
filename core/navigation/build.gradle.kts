plugins {
    id("dev.pott.android.lib.compose")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
    namespace = "dev.pott.abonity.core.navigation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.assertk)
    testImplementation(libs.mockk)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher) {
        because(
            "Only needed to run tests in a version of IntelliJ IDEA that bundles older versions",
        )
    }
}
