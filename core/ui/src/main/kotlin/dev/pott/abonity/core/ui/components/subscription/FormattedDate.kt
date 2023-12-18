package dev.pott.abonity.core.ui.components.subscription

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.TextUnit
import dev.pott.abonity.core.ui.preview.PreviewCommonLocaleConfig
import dev.pott.abonity.core.ui.util.rememberDefaultLocale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun FormattedDate(
    date: LocalDate,
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
    val localizedDate = rememberFormattedDate(date)
    Text(
        text = localizedDate,
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
fun rememberFormattedDate(epochMilliseconds: Long): String {
    val date = remember(epochMilliseconds) {
        Instant.fromEpochMilliseconds(epochMilliseconds)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
    return rememberFormattedDate(date = date)
}

@Composable
fun rememberFormattedDate(date: LocalDate): String {
    val locale = rememberDefaultLocale()
    val localizedDate = remember(locale, date) {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        date.toJavaLocalDate().format(formatter)
    }
    return localizedDate
}

@Suppress("MagicNumber")
@PreviewCommonLocaleConfig
@Composable
private fun FormattedDatePreview() {
    FormattedDate(date = LocalDate(2022, 12, 1))
}
