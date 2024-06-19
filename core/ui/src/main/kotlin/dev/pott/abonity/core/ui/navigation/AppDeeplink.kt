package dev.pott.abonity.core.ui.navigation

object AppDeeplink {
    const val SCHEME = "android-app://"
    const val HOST = "dev.pott.abonity"

    operator fun invoke(path: String): String = "$HOST${SCHEME}$path"
}
