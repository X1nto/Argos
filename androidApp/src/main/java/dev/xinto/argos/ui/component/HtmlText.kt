package dev.xinto.argos.ui.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewFontScale
import dev.xinto.argos.ui.theme.ArgosTheme
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.safety.Safelist

@OptIn(ExperimentalTextApi::class)
@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current,
) {
    val coloredStyle = style.merge(color = color)
    val linkColor = MaterialTheme.colorScheme.primary
    val uriHandler = LocalUriHandler.current
    val annotatedString = remember(text, linkColor) {
        val parsed = Jsoup.parse(text)
        buildAnnotatedString {
            parsed.body().childNodes().forEach {
                when (it) {
                    is Element -> {
                        when (it.tagName()) {
                            "a" -> {
                                withAnnotation(UrlAnnotation(it.attr("href"))) {
                                    withStyle(SpanStyle(color = linkColor)) {
                                        append(it.text())
                                    }
                                }
                            }
                            "br" -> appendLine()
                            "b" -> {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(it.text())
                                }
                            }
                        }
                    }
                    is TextNode -> {
                        append(Jsoup.clean(it.text(), Safelist.none()))
                    }
                }
            }
        }
    }
    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = {
            uriHandler.openUri(annotatedString.getUrlAnnotations(it, it)[0].item.url)
        },
        style = coloredStyle
    )
}

@Composable
@PreviewFontScale
fun HtmlText_Preview() {
    ArgosTheme {
        Surface {
            HtmlText(
                text = "test1<p>tes2</p<br><b>test3</b>"
            )
        }
    }
}