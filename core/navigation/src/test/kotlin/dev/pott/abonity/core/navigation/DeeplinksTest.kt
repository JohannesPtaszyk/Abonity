package dev.pott.abonity.core.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class DeeplinksTest {

    @Test
    fun `GIVEN subscription deeplink with id WHEN createSubscriptionDeeplink THEN return correct deeplink`() {
        val deeplink = Deeplinks.createSubscriptionDeeplink(123L)
        assertThat(deeplink).isEqualTo("android-app://dev.pott.abonity/subscription?detailId=123")
    }
}
