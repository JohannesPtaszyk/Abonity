plugins {
    id("dev.pott.android.lib.compose")
    id("dev.pott.hilt")
}

android {
    namespace = "dev.pott.abonity.feature.settings"
}

dependencies {
    implementation(projects.navigation)
    implementation(projects.core.domain)
    implementation(projects.core.ui)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    testImplementation(projects.common.test)
    testImplementation(projects.core.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
