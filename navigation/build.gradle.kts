plugins {
    id("dev.pott.android.lib.compose")
}

android {
    namespace = "dev.pott.abonity.feature.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kermit)
}
