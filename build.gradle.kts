buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.google.oss.licenses.plugin) {
            exclude(group = "com.google.protobuf")
        }
    }
}

// Remove after https://github.com/touchlab/Kermit/issues/383
subprojects {
    configurations
        .matching { it.name.endsWith("TestRuntimeClasspath") }
        .configureEach {
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-test-junit")
        }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.sonar) apply false
    alias(libs.plugins.compose.compiler.report) apply false
    id("dev.pott.project")
}
