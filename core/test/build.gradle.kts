plugins {
    id("dev.pott.jvm.lib")
}

dependencies {
    implementation(projects.core.entity)
    implementation(projects.core.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.faker)

    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher) {
        because(
            "Only needed to run tests in a version of IntelliJ IDEA that bundles older versions",
        )
    }
}
