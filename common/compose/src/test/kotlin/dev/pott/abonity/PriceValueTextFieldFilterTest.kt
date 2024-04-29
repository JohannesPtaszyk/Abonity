package dev.pott.abonity

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.pott.abonity.common.text.PriceValueTextFieldFilter
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class PriceValueTextFieldFilterTest {

    @ParameterizedTest
    @MethodSource("provideTestData")
    fun testFilterBehavior(data: FilterTestData) {
        val fakeCallback = FakeCallback()
        val filter = PriceValueTextFieldFilter(fakeCallback)

        filter.invoke(data.input)

        assertThat(fakeCallback.called).isEqualTo(data.expectedCalled)
        assertThat(fakeCallback.text).isEqualTo(data.expectedText)
    }

    private class FakeCallback(var text: String? = null) : (String) -> Unit {

        var called: Boolean = false
        override fun invoke(text: String) {
            this.called = true
            this.text = text
        }
    }

    data class FilterTestData(
        val input: String,
        val expectedCalled: Boolean,
        val expectedText: String? = null,
    )

    companion object {
        @JvmStatic
        @Suppress("UnusedPrivateMember")
        private fun provideTestData(): List<FilterTestData> {
            return listOf(
                FilterTestData("", true, ""),
                FilterTestData("123", true, "123"),
                FilterTestData("abc.!@", false),
                FilterTestData("abc41", false),
                FilterTestData("1.99", true, "1.99"),
                FilterTestData("1,99", true, "1,99"),
                FilterTestData("22131.939", false),

            )
        }
    }
}
