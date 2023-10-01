plugins {
    id("dev.pott.android.lib.compose")
}

android {
    namespace = "dev.pott.abonity.feature.subscription"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
}