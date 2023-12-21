plugins {
    id("dev.pott.android.lib.compose")
    id("dev.pott.hilt")
}

android {
    namespace = "dev.pott.abonity.feature.settings"
}

dependencies {
    implementation(projects.navigation)
    implementation(projects.core.ui)
    implementation(projects.core.domain)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
