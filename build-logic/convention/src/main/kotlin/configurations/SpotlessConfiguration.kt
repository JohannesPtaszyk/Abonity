package configurations

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.applySpotless() {
    subprojects {
        plugins.apply(SpotlessPlugin::class)
        configure<SpotlessExtension> {
            kotlin {
                target("**/*.kt")
                targetExclude("**/build/**", "**/tmp/**", "**/.gradle/**")

                val ktlintVersion = libs.findVersion("ktlint").get().toString()
                ktlint(ktlintVersion).editorConfigOverride(mapOf("android" to "true"))
            }
            format("xml") {
                target("**/*.xml")
            }
        }
    }
}
