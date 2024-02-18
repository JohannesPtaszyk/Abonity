package util

import io.github.g00fy2.versioncompare.Version
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.slf4j.LoggerFactory
import javax.inject.Inject

private const val FALLBACK_VERSION_NAME = "0.0.0"
private const val RELEASE_BRANCH_NAMING_PATTERN = "release/<major.minor.patch>"

private const val VERSION_PREFIX = "v"

abstract class GitAppVersionValueSource : ValueSource<String, ValueSourceParameters.None> {

    private val logger = LoggerFactory.getLogger("GitAppVersionValueSource")

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val commitHash = execOperations.execToString("git", "rev-parse", "--short", "HEAD")
        val branchName = execOperations.execToString("git", "rev-parse", "--abbrev-ref", "HEAD")
        val versionName = if (branchName.startsWith("release")) {
            if (branchName.contains("/")) {
                branchName.split("/").last()
            } else {
                error(
                    "Release branch name is wrong. " +
                        "Branch name must follow naming pattern $RELEASE_BRANCH_NAMING_PATTERN",
                )
            }
        } else {
            logger.warn(
                "No release branch checked out " +
                    "or naming does not match $RELEASE_BRANCH_NAMING_PATTERN. " +
                    "Using last version tag as version name.",
            )
            try {
                getLatestVersionFromTags()
            } catch (e: Exception) {
                logger.error(
                    "Could not describe tags: ${e.message}, " +
                        "using $FALLBACK_VERSION_NAME' as version name",
                )
                FALLBACK_VERSION_NAME
            }
        }
        val versionNameWithCommitHash = "$versionName-$commitHash"
        println("Version Name: $versionNameWithCommitHash")
        return versionNameWithCommitHash
    }

    private fun getLatestVersionFromTags(): Version {
        return execOperations.execToString("git", "tag")
            .split(System.lineSeparator())
            .filter { it.isNotBlank() && it.startsWith(VERSION_PREFIX) }
            .map { Version(it.removePrefix(VERSION_PREFIX)) }
            .maxBy { it }
    }
}
