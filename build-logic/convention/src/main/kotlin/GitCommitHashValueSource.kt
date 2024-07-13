import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import util.execToString
import javax.inject.Inject

abstract class GitCommitHashValueSource : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String =
        execOperations.execToString(
            "git",
            "rev-parse",
            "--short",
            "HEAD",
        ).removeSuffix(System.lineSeparator())
}
