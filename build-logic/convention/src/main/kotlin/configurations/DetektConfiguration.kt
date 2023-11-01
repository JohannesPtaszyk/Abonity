package configurations

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType

internal fun Project.applyDetekt() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
        output.set(layout.buildDirectory.file("reports/detekt/merge.sarif"))
    }
    val detektReportMergeXml by tasks.registering(ReportMergeTask::class) {
        output.set(layout.buildDirectory.file("reports/detekt/merge.xml"))
    }

    subprojects {
        pluginManager.apply(DetektPlugin::class)

        configure<DetektExtension> {
            buildUponDefaultConfig = true
            parallel = true
            config.setFrom(
                files(
                    "$rootDir/.detekt/compose-detekt-config.yml",
                    "$rootDir/.detekt/performance-detekt-config.yml",
                    "$rootDir/.detekt/tests-detekt-config.yml",
                ),
            )
        }

        dependencies {
            add("detektPlugins", libs.findLibrary("detekt.composeRules").get())
        }

        tasks.withType<Detekt>().configureEach {
            jvmTarget = projectJavaVersion.toString()
            reports {
                xml.required.set(true)
                html.required.set(false)
                sarif.required.set(true)
                txt.required.set(false)
                md.required.set(false)
            }
            finalizedBy(detektReportMergeSarif, detektReportMergeXml)
        }

        detektReportMergeSarif {
            input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
        }

        detektReportMergeXml {
            input.from(tasks.withType<Detekt>().map { it.xmlReportFile })
        }

        tasks.withType<DetektCreateBaselineTask>().configureEach {
            jvmTarget = projectJavaVersion.toString()
        }
    }
}
