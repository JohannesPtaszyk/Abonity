package tasks

import io.github.g00fy2.versioncompare.Version
import org.gradle.api.tasks.TaskAction
import util.RC_TAG_BUID_NUMBER_SEPERATOR
import util.RC_TAG_PREFIX
import util.RELEASE_TAG_PREFIX
import util.execToString
import util.getRcTags
import util.getTags

abstract class CreateReleaseTagTask : AbstractTagTask() {

    @TaskAction
    fun createReleaseTagFromLatestRc() {
        val latestTag = execOperations.getRcTags()
            .map {
                val versionString = it.removePrefix(RC_TAG_PREFIX)
                    .split(RC_TAG_BUID_NUMBER_SEPERATOR)
                    .first()
                Version(versionString)
            }
            .maxOfOrNull { it }
            ?.toString()

        val version = latestTag ?: error("Could not create release tag, due to missing RC tag")
        val newTag = "$RELEASE_TAG_PREFIX$version"
        if (execOperations.getTags().contains(newTag)) {
            logger.warn("Tag $newTag was already created. Did not create new release tag")
            return
        }

        execOperations.execToString("git", "tag", newTag)
        pushTag(newTag)
    }

    companion object {
        const val NAME = "createReleaseTagFromLatestRc"
    }
}
