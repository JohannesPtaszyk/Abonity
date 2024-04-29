plugins {
    id("dev.pott.android.lib.compose")
}

android {
    namespace = "dev.pott.abonity.core.ui"
}

dependencies {
    api(libs.androidx.compose.material.iconsExtended)

    implementation(projects.core.domain)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.kermit)

    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
