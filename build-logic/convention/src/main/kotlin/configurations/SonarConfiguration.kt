package configurations

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.sonarqube.gradle.SonarExtension
import org.sonarqube.gradle.SonarQubePlugin

fun Project.applySonar() {
    pluginManager.apply(SonarQubePlugin::class)
    configure<SonarExtension> {
        properties {
            property("sonar.sourceEncoding", "UTF-8")
            property("sonar.projectKey", "JohannesPtaszyk_Abonity")
            property("sonar.organization", "johannesptaszyk")
            property("sonar.host.url", "https://sonarcloud.io")
            property(
                "sonar.kotlin.detekt.reportPaths",
                layout.buildDirectory.file("reports/detekt/merge.xml").get().asFile.path,
            )
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                layout.buildDirectory.file("reports/kover/report.xml").get().asFile.path,
            )
        }
    }
}
