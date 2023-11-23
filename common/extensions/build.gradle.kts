plugins {
    id("dev.pott.jvm.lib")
}

dependencies {
    implementation(platform(libs.junit.bom))
    implementation(libs.kotlinx.datetime)
    implementation(libs.junit.jupiter.api)
}
