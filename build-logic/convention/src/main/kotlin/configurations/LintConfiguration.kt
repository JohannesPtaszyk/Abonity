package configurations

import com.android.build.api.dsl.Lint
import org.gradle.api.Project

fun Project.configureLint(lint: Lint) {
    with(lint) {
        xmlReport = true
        checkDependencies = true
        xmlOutput = layout.buildDirectory.file("reports/lint/result.xml").get().asFile
    }
}
