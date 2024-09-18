plugins {
    id("dev.pott.android.lib.compose")
}

android {
    namespace = "dev.pott.abonity.core.navigation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.assertk)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockk)
}
