package configurations

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun Project.applyDetekt() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    pluginManager.apply(DetektPlugin::class)

    configure<DetektExtension> {
        parallel = true
        config.setFrom(
            files(
                "$rootDir/.detekt/compose-detekt-config.yml",
                "$rootDir/.detekt/performance-detekt-config.yml",
                "$rootDir/.detekt/tests-detekt-config.yml",
            ),
        )
        buildUponDefaultConfig = true
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = projectJavaVersion.toString()
        reports {
            xml {
                required.set(true)
                outputLocation.set(
                    file("${layout.buildDirectory.asFile.get()}/reports/detekt/detekt.xml"),
                )
            }
            html.required.set(true)
            txt.required.set(false)
        }
        dependencies {
            add("detektPlugins", libs.findLibrary("detekt.composeRules").get())
        }
    }
}
