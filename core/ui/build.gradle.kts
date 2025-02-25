plugins {
    id("dev.pott.android.lib.compose")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "dev.pott.abonity.core.ui"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.androidx.compose.material.iconsExtended)

    implementation(projects.core.domain)

    implementation(libs.ads)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.kermit)
    implementation(libs.androidx.ui.text.google.fonts)

    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher) {
        because(
            "Only needed to run tests in a version of IntelliJ IDEA that bundles older versions",
        )
    }
}
