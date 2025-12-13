package dev.pott.abonity.feature.subscription.add

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.Currency
import java.util.Locale

class AddFormStateTest {

    @TestFactory
    fun `GIVEN locale THEN initialize with correct currency`(): List<DynamicTest> =
        mapOf<Locale, Currency>(
            Locale.GERMANY to Currency.getInstance("EUR"),
            Locale.US to Currency.getInstance("USD"),
            Locale.FRANCE to Currency.getInstance("EUR"),
            Locale.CHINA to Currency.getInstance("CNY"),
            Locale.JAPAN to Currency.getInstance("JPY"),
            Locale.UK to Currency.getInstance("GBP"),
            Locale.CANADA to Currency.getInstance("CAD"),
            Locale.TAIWAN to Currency.getInstance("TWD"),
            Locale.KOREA to Currency.getInstance("KRW"),
            Locale.ENGLISH to Currency.getInstance("EUR"),
            Locale.GERMAN to Currency.getInstance("EUR"),
        ).map {
            DynamicTest.dynamicTest(
                "GIVEN locale ${it.key} WHEN initialize THEN currency is ${it.value.displayName}",
            ) {
                Locale.setDefault(it.key)
                val addFormState = AddFormState()
                assertThat(addFormState.currency).isEqualTo(it.value)
            }
        }
}
