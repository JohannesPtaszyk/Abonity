package util

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class GitAppVersionValueSource : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val commitHash = execOperations.execToString(
            "git",
            "rev-parse",
            "--short",
            "HEAD",
        ).removeSuffix(System.lineSeparator())
        val versionName = execOperations.getVersionNameFromBranchName()
        val versionNameWithCommitHash = "$versionName-$commitHash"
        println("Version Name: $versionNameWithCommitHash")
        return versionNameWithCommitHash
    }
}
