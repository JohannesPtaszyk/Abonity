package dev.pott.abonity.core.navigation

import android.os.Bundle
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class SubscriptionIdNavTypeTest {

    @Test
    fun `GIVEN a bundle containing a subscription id WHEN get() is called THEN the subscription id instance is returned`() {
        val key = "test_parameter"
        val subscriptionId = SubscriptionId(1)
        val bundle = mockk<Bundle> {
            every { getLong(key, -1) } returns subscriptionId.value
        }

        val result = SubscriptionIdNavType[bundle, key]

        assertThat(result).isEqualTo(subscriptionId)
    }

    @Test
    fun `GIVEN a bundle not containing a subscription id WHEN get() is called THEN null is returned`() {
        val key = "test_parameter"
        val bundle = mockk<Bundle> {
            every { getLong(key, -1) } returns -1
        }

        val result = SubscriptionIdNavType[bundle, key]

        assertThat(result).isEqualTo(null)
    }

    @Test
    fun `GIVEN a subscription id WHEN parseValue() is called THEN subscription id instance is returned`() {
        val value = "1"

        val result = SubscriptionIdNavType.parseValue(value)

        assertThat(result).isEqualTo(SubscriptionId(1L))
    }

    @Test
    fun `GIVEN a string that cannot be parsed to a subscription id WHEN parseValue() is called THEN IllegalArgumentException is thrown`() {
        val value = "test"

        assertFailure { SubscriptionIdNavType.parseValue(value) }
            .isInstanceOf(IllegalArgumentException::class)
    }

    @Test
    fun `GIVEN a subscription id WHEN put() is called THEN the value is put in the bundle`() {
        val key = "test_parameter"
        val bundle = mockk<Bundle> {
            every { putLong(key, any()) } returns Unit
        }
        val subscriptionId = SubscriptionId(1)

        SubscriptionIdNavType.put(bundle, key, subscriptionId)

        verify { bundle.putLong(key, subscriptionId.value) }
    }
}
