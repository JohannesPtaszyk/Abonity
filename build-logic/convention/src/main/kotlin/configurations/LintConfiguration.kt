package configurations

import com.android.build.api.dsl.Lint
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureLint(lint: Lint) {
    with(lint) {
        xmlReport = true
        checkDependencies = true
        xmlOutput = layout.buildDirectory.file("reports/lint/result.xml").get().asFile
    }

    dependencies {
        add("lintChecks", libs.findLibrary("android.lint.security").get())
    }
}
