plugins {
    id("dev.pott.android.lib.compose")
}

android {
    namespace = "dev.pott.abonity.common.compose"
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertk)
}
