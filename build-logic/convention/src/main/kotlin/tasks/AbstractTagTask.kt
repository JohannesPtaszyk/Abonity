package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import util.execToString
import javax.inject.Inject

abstract class AbstractTagTask : DefaultTask() {

    @set:Option(
        option = "push",
        description = "If true the gradle task will attempt to execute 'git push origin <tag>' " +
            "to push the newly create tag to your origin",
    )
    @get:Input
    abstract var push: Boolean

    @get:Inject
    abstract val execOperations: ExecOperations

    fun pushTag(tag: String) {
        if (push) {
            execOperations.execToString("git", "push", "origin", tag)
        }
    }
}
