package plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.samples.apps.nowinandroid.configureKotlinAndroid
import configurations.applyDetekt
import configurations.configureAndroidCompose
import configurations.configureGradleManagedDevices
import configurations.configureJacoco
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.firebase.crashlytics")
                apply("com.google.firebase.firebase-perf")
                apply("dev.pott.android.lint")
                apply("org.gradle.jacoco")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.gms.google-services")
                apply("com.google.android.gms.oss-licenses-plugin")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk =
                    libs.findVersion("targetSdkVersion").get().toString().toInt()
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                configureGradleManagedDevices(this)

                buildTypes.configureEach {
                    // Disable the Crashlytics mapping file upload. This feature should only be
                    // enabled if a Firebase backend is available and configured in
                    // google-services.json.
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = false
                    }
                }
            }

            extensions.getByType<ApplicationAndroidComponentsExtension>().apply {
                configureJacoco(this)
            }

            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                "implementation"(libs.findLibrary("firebase.analytics").get())
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
                "implementation"(libs.findLibrary("firebase.performance").get())
            }

            applyDetekt()
        }
    }
}
