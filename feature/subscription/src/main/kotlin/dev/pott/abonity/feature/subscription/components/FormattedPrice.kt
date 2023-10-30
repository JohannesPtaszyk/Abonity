package dev.pott.abonity.feature.subscription.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.ui.util.getDefaultLocale
import java.text.NumberFormat
import java.util.Currency

@Composable
fun FormattedPrice(
    price: Price,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val formatted by rememberFormattedPrice(
        value = price.value,
        currency = price.currency,
    )
    Text(
        text = formatted,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Composable
fun rememberFormattedPrice(value: Double, currency: Currency): State<String> {
    val locale = getDefaultLocale()
    val format =
        remember(locale) {
            NumberFormat.getCurrencyInstance(locale)
        }
    return remember(value, currency) {
        derivedStateOf {
            format.currency = currency
            format.format(value)
        }
    }
}

@Preview(locale = "de")
@Composable
private fun DeFormattedPricePreview(
    @PreviewParameter(FormattedPricePreviewProvider::class) price: Price,
) {
    FormattedPrice(price = price)
}

@Preview(locale = "en")
@Composable
private fun EnFormattedPricePreview(
    @PreviewParameter(FormattedPricePreviewProvider::class) price: Price,
) {
    FormattedPrice(price = price)
}

private class FormattedPricePreviewProvider : PreviewParameterProvider<Price> {
    override val values: Sequence<Price>
        get() {
            return sequenceOf(
                Price(99.99, Currency.getInstance("EUR")),
                Price(99.99, Currency.getInstance("USD")),
                Price(99.99, Currency.getInstance("USD")),
                Price(99.99, Currency.getInstance("CLF")),
                Price(99.99, Currency.getInstance("GBP")),
                Price(99.99, Currency.getInstance("MOP")),
            )
        }
}
