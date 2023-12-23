package dev.xinto.argos.ui.component

import android.annotation.SuppressLint
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import dev.xinto.argos.ui.theme.ArgosTheme
import org.intellij.lang.annotations.Language

@Composable
fun MaterialHtmlText2(
    @Language("HTML")
    text: String,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true
) {
    HtmlText2(
        modifier = modifier,
        text = text,
        typography = HtmlText2Defaults.material3Typography(),
        userScrollEnabled = userScrollEnabled
    )
}

//Absolutely fucking horrible component
//ADD WEBVIEW TO FOUNDATION ALREADY
@SuppressLint("ClickableViewAccessibility")
@Composable
fun HtmlText2(
    @Language("HTML")
    text: String,
    typography: HtmlText2Typography,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
) {
    val textCss = typography.asCss(LocalDensity.current)
    AndroidView(
        modifier = modifier,
        factory = {
            val webView = WebView(it).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                isLongClickable = false
                setOnLongClickListener { true }
                isHapticFeedbackEnabled = false
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            FrameLayout(it).apply { //FrameLayout needed to prevent crashes
                addView(webView)
            }
        },
        update = {
            val webView = it.children.first() as WebView

            @Language("HTML")
            val htmlText = """
                <html>
                  <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <style>
                      $textCss
                    </style>
                  </head>
                  <body>
                    $text
                  </body>
                </html>
            """.trimIndent()
            webView.loadData(htmlText, "text/html; charset=utf-8", "UTF-8")

            if (userScrollEnabled) {
                webView.setOnTouchListener { v, event ->
                    webView.requestDisallowInterceptTouchEvent(true)
                    false
                }
            } else {
                webView.setOnTouchListener(null)
            }
        },
        onReset = {
            //enable reuse
        }
    )
}

@PreviewLightDark
@Composable
fun HtmlText2_Preview() {
    ArgosTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MaterialHtmlText2(
                modifier = Modifier
                    .height(500.dp)
                    .width(300.dp),
                text = """
                    <h1>H1 headingg</h1>
                    <h2>H2 heading</h2>
                    <h3>H3 heading</h3>
                    <h4>H4 heading</h4>
                    <h5>H5 heading</h5>
                    <h6>H6 heading</h6>
                    <p>This is a paragraph.</p>

                    <table>
                      <tbody>
                        <tr>
                          <th>title1</th>
                          <th>title2</th>
                          <th>title3</th>
                          <th>title4</th>
                          </tr>
                        <tr>
                          <td>test1</td>
                          <td>test2</td>
                          <td>test3</td>
                          <td>test4</td>
                        </tr>
                      </tbody>
                    </table>
                """.trimIndent()
            )
        }
    }
}

object HtmlText2Defaults {

    @Composable
    fun material3Typography(color: Color = LocalContentColor.current): HtmlText2Typography {
        return HtmlText2Typography(
            h1 = MaterialTheme.typography.displaySmall.toHtmlTextStyle(color),
            h2 = MaterialTheme.typography.headlineLarge.toHtmlTextStyle(color),
            h3 = MaterialTheme.typography.headlineMedium.toHtmlTextStyle(color),
            h4 = MaterialTheme.typography.headlineSmall.toHtmlTextStyle(color),
            h5 = MaterialTheme.typography.titleLarge.toHtmlTextStyle(color),
            body = MaterialTheme.typography.bodyLarge.toHtmlTextStyle(color)
        )
    }

    private fun TextStyle.toHtmlTextStyle(color: Color = Color.Unspecified): HtmlText2TextStyle {
        return HtmlText2TextStyle(
            fontSize = fontSize,
            textColor = color.takeOrElse { this.color },
            fontWeight = fontWeight ?: FontWeight.Normal
        )
    }
}

private interface CssRepresentable {
    @Language("CSS")
    fun asCss(density: Density): String
}

@Immutable
data class HtmlText2TextStyle(
    val fontSize: TextUnit,
    val textColor: Color,
    val fontWeight: FontWeight,
) : CssRepresentable {

    @Language("CSS")
    override fun asCss(density: Density): String {
        val (r, g, b, a) = textColor
        val cssFontSize = "${fontSize.value}" + if (fontSize.isEm) "em" else "px"
        return """
            color: rgba(${r * 255f}, ${g * 255f}, ${b * 255f}, $a);
            font-weight: ${fontWeight.weight};
            font-size: $cssFontSize;
        """.trimIndent()
    }
}

@Immutable
data class HtmlText2Typography(
    val h1: HtmlText2TextStyle,
    val h2: HtmlText2TextStyle,
    val h3: HtmlText2TextStyle,
    val h4: HtmlText2TextStyle,
    val h5: HtmlText2TextStyle,
    val body: HtmlText2TextStyle
) : CssRepresentable {

    @Language("CSS")
    override fun asCss(density: Density): String {
        return """
            * {
                margin: 0; 
                padding: 0;
            }
            h1 {
                ${h1.asCss(density)}
            }
            h2 {
                ${h2.asCss(density)}
            }
            h3 {
                ${h3.asCss(density)}
            }
            h4 {
                ${h4.asCss(density)}
            }
            h5 {
                ${h5.asCss(density)}
            }
            p, body {
                ${body.asCss(density)}
            }
        """.trimIndent()
    }

}