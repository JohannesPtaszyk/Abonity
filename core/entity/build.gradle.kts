plugins {
    id("dev.pott.jvm.lib")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.assertk)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
