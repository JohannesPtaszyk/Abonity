plugins {
    id("dev.pott.jvm.lib")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter.api)
    implementation(libs.kotlinx.coroutines.test)
}
