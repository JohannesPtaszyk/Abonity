plugins {
    id("dev.pott.android.app")
    id("dev.pott.hilt")
    id("dev.pott.android.room")
}

android {
    namespace = "dev.pott.abonity.app"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "dev.pott.abonity"
        versionCode = 1
        versionName = "2023.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testApplicationId = "$applicationId.test"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(projects.core.local)
    implementation(projects.core.domain)
    implementation(projects.core.entity)
    implementation(projects.core.ui)
    implementation(projects.feature.home)
    implementation(projects.feature.subscription)
    implementation(projects.feature.settings)
    implementation(projects.navigation)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.google.oss.licenses)
    implementation(libs.hilt.android)
    implementation(libs.kermit)

    testImplementation(projects.core.test)
    testImplementation(projects.common.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertk)

    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)

    testRuntimeOnly(libs.junit.jupiter.engine)
}
