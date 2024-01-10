plugins {
    id("dev.pott.jvm.lib")
}

dependencies {
    api(projects.core.entity)
    implementation(projects.common.extensions)
    implementation(projects.common.injection)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.kermit)

    testImplementation(projects.core.test)
    testImplementation(projects.common.test)
    testImplementation(libs.assertk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

}
