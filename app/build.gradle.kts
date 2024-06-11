import com.google.firebase.appdistribution.gradle.AppDistributionExtension
import util.GitAppVersionValueSource
import util.RcTagVersionCodeValueSource

plugins {
    id("dev.pott.android.app")
    id("dev.pott.hilt")
    id("dev.pott.android.room")
    id(libs.plugins.gms.get().pluginId)
    id(libs.plugins.firebase.distribution.get().pluginId)
    id(libs.plugins.secrets.get().pluginId)
    id(libs.plugins.play.publisher.get().pluginId)
    // id(libs.plugins.appsweep.get().pluginId)
}

android {
    namespace = "dev.pott.abonity.app"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "dev.pott.abonity"
        versionCode = providers.of(RcTagVersionCodeValueSource::class) {}.get()
        versionName = providers.of(GitAppVersionValueSource::class) {}.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testApplicationId = "$applicationId.test"
        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations.addAll(listOf("en", "de", "fr"))

        setProperty("archivesBaseName", "$applicationId($versionName)-$versionCode")
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        val release by creating {
            storeFile = file("../abonity.keystore")
            storePassword = System.getenv("ABONITY_KEY_STORE_PASSWORD") ?: ""
            keyAlias = System.getenv("ABONITY_KEY_STORE_KEY_ALIAS") ?: ""
            keyPassword = System.getenv("ABONITY_KEY_STORE_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            configure<AppDistributionExtension> {
                artifactType = "APK"
                groups = "internal, friends-&-family"
                serviceCredentialsFile = "firebase-service-account.json"
            }
        }
        val release by getting {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
            configure<AppDistributionExtension> {
                artifactType = "AAB"
                groups = "internal, friends-&-family, external"
                serviceCredentialsFile = "firebase-service-account.json"
            }
        }
    }
}

play {
    serviceAccountCredentials.set(file("../google-play-publisher-account.json"))
}

dependencies {
    implementation(projects.core.local)
    implementation(projects.core.domain)
    implementation(projects.core.entity)
    implementation(projects.core.ui)
    implementation(projects.common.injection)
    implementation(projects.feature.home)
    implementation(projects.feature.subscription)
    implementation(projects.feature.settings)
    implementation(projects.feature.legal)
    implementation(projects.navigation)

    implementation(libs.ads)
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.cloud.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.inapp.messaging)
    implementation(libs.google.oss.licenses)
    implementation(libs.hilt.android)
    implementation(libs.kermit)
    implementation(libs.androidx.dataStore)

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
