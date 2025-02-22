plugins {
    id("dev.pott.android.lib.compose")
    id("dev.pott.hilt")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
    namespace = "dev.pott.abonity.feature.home"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.ui)

    implementation(libs.accompanist.permission)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    implementation(libs.kermit)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(projects.core.test)
    testImplementation(projects.common.test)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher) {
        because(
            "Only needed to run tests in a version of IntelliJ IDEA that bundles older versions",
        )
    }
}
