plugins {
    id("dev.pott.android.lib")
    id("dev.pott.android.room")
    id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
    namespace = "dev.pott.abonity.core.local"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.androidx.dataStore)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.javax.inject)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(projects.core.test)
}
