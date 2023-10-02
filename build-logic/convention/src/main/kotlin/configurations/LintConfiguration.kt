package configurations

import com.android.build.api.dsl.Lint

fun Lint.configureLint() {
    xmlReport = true
    checkDependencies = true
}
