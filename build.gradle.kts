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

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler.report) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.distribution) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.play.publisher) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.sonar) apply false
    alias(libs.plugins.spotless) apply false
    id("dev.pott.project")
}

dependencies {
    subprojects.forEach {
        it.pluginManager.withPlugin("org.jetbrains.kotlinx.kover") {
            add("kover", it)
        }
    }
}
