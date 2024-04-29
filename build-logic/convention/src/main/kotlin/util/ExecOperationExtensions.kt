package util

import io.github.g00fy2.versioncompare.Version
import org.gradle.process.ExecOperations
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

private const val FALLBACK_VERSION_NAME = "0.0.0"
private const val RELEASE_BRANCH_NAMING_PATTERN = "release/<major.minor.patch>"

private const val RELEASE_PREFIX = "release"

fun ExecOperations.execToString(vararg args: String): String {
    val output = ByteArrayOutputStream()
    exec {
        commandLine(*args)
        standardOutput = output
    }
    return String(output.toByteArray(), Charset.defaultCharset())
}

fun ExecOperations.getLatestVersionFromTags(): Version {
    return execToString("git", "tag")
        .split(System.lineSeparator())
        .filter { it.isNotBlank() && it.startsWith(RELEASE_TAG_PREFIX) }
        .map { Version(it.removePrefix(RELEASE_TAG_PREFIX)) }
        .maxBy { it }
}

fun ExecOperations.getRcTags(): List<String> {
    return getTags()
        .split(System.lineSeparator())
        .filter { it.isNotBlank() && it.startsWith(RC_TAG_PREFIX, ignoreCase = true) }
}

fun ExecOperations.getTags() = execToString("git", "tag")

fun ExecOperations.getVersionNameFromBranchName(): String {
    val logger = LoggerFactory.getLogger("getVersionNameFromBranchName")

    val branchName = execToString(
        "git",
        "rev-parse",
        "--abbrev-ref",
        "HEAD",
    ).removeSuffix(System.lineSeparator())

    return if (branchName.startsWith(RELEASE_PREFIX)) {
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
            getLatestVersionFromTags().toString()
        } catch (e: Exception) {
            logger.error(
                "Could not describe tags: ${e.message}, " +
                    "using $FALLBACK_VERSION_NAME as version name",
            )
            FALLBACK_VERSION_NAME
        }
    }
}
