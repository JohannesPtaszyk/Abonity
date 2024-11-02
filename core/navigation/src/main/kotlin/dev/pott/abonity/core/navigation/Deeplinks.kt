package dev.pott.abonity.core.navigation

object Deeplinks {
    private const val SCHEME = "android-app://"
    private const val HOST = "dev.pott.abonity"
    const val ADD_SUBSCRIPTION = "$SCHEME$HOST/add"
    const val SUBSCRIPTION = "$SCHEME$HOST/subscription"
    fun createSubscriptionDeeplink(id: Long) = "$SCHEME$HOST/subscription?detailId=$id"
}
