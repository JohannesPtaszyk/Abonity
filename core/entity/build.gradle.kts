plugins {
    id("dev.pott.jvm.lib")
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.kotlin.stdlib)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.assertk)
}
