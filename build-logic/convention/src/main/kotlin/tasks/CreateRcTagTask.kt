package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import util.RC_TAG_BUID_NUMBER_SEPERATOR
import util.RC_TAG_PREFIX
import util.execToString
import util.getRcTags
import util.getVersionNameFromBranchName
import javax.inject.Inject

abstract class CreateRcTagTask : DefaultTask() {

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun createRcTag() {
        val versionFromBranch = execOperations.getVersionNameFromBranchName()
        val latestTag = execOperations.getRcTags()
            .filter { it.contains(versionFromBranch) }
            .maxOfOrNull { it }
        val newTag = if (latestTag != null) {
            val (tag, buildNumber) = latestTag.split(RC_TAG_BUID_NUMBER_SEPERATOR)
            "$tag$RC_TAG_BUID_NUMBER_SEPERATOR${(buildNumber.toInt() + 1)}"
        } else {
            "$RC_TAG_PREFIX$versionFromBranch${RC_TAG_BUID_NUMBER_SEPERATOR}1"
        }
        execOperations.execToString("git", "tag", newTag)
        execOperations.execToString("git", "push", "origin", newTag)
    }

    companion object {
        const val NAME = "createRcTag"
    }
}
