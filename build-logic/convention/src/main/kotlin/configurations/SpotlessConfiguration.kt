package configurations

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.applySpotless() {
    plugins.apply(SpotlessPlugin::class)
    configureSpotless()
}

private fun Project.configureSpotless() {
    configure<SpotlessExtension> {
        format("misc") {
            // define the files to apply `misc` to
            target("**/*.gradle', '**/*.md', '**/.gitignore")

            // define the steps to apply to those files
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            target("**/*.kt", "**/*.kts")
            targetExclude("**/build/**", "**/tmp/**", "**/.gradle/**")

            val ktlintVersion = libs.findVersion("ktlint").get().toString()
            ktlint(ktlintVersion)
        }

        format("xml") {
            target("**/*.xml")
        }

        json {
            target("src/**/*.json")
            simple()
        }

        format("toml") {
            target("**/*.toml")
            prettier(
                mapOf(
                    "prettier" to "latest",
                    "prettier-plugin-toml" to "latest",
                ),
            ).config(
                mapOf("parser" to "toml", "plugins" to listOf("prettier-plugin-toml")),
            )
        }
    }
}
