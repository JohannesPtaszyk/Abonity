package plugins

import com.android.build.api.dsl.LibraryExtension
import configurations.applyKoverAndroid
import configurations.configureGradleManagedDevices
import configurations.configureKotlinAndroid
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

@Suppress("unused")
class AndroidLibConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("dev.pott.android.lint")
                apply("tech.apter.junit5.jupiter.robolectric-extension-gradle-plugin")
            }
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureGradleManagedDevices(this)
                @Suppress("UnstableApiUsage")
                testFixtures.enable = true
            }
            applyKoverAndroid()
            dependencies {
                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))
            }
        }
    }
}
