plugins {
    id("dev.pott.android.lib.compose")
    id("dev.pott.hilt")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
    namespace = "dev.pott.abonity.feature.subscription"
}

dependencies {
    implementation(projects.common.compose)
    implementation(projects.common.injection)
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.navigation)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kermit)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(projects.core.test)
    testImplementation(projects.common.test)
    testImplementation(libs.androidx.navigation.testing)
}
