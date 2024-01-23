package dev.xinto.argos.ui.component

import android.annotation.SuppressLint
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    HtmlText2(
        modifier = modifier,
        text = text,
        contentStyle = HtmlText2Defaults.material3Typography(),
        userScrollEnabled = userScrollEnabled,
        contentPadding = contentPadding
    )
}

//Absolutely fucking horrible component
//ADD WEBVIEW TO FOUNDATION ALREADY
@SuppressLint("ClickableViewAccessibility")
@Composable
fun HtmlText2(
    @Language("HTML")
    text: String,
    contentStyle: HtmlText2ContentStyle,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textCss = remember(density, layoutDirection) {
        contentStyle.asCss(density, layoutDirection)
    }
    val paddingCss = remember(layoutDirection) {
        //li margin required to indent bullet-points
        """
          body {
            padding: ${contentPadding.toCss(layoutDirection)}
          }
          
          li {
            margin-inline-start: ${contentPadding.calculateStartPadding(layoutDirection).value}px
          }
        """.trimIndent()
    }
    val escapedText = remember {
        text.replace("#", "%23")
    }
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
                      $paddingCss
                    </style>
                  </head>
                  <body>
                    $escapedText
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
    fun material3Typography(color: Color = LocalContentColor.current): HtmlText2ContentStyle {
        return HtmlText2ContentStyle(
            h1 = MaterialTheme.typography.displaySmall.toHtmlTextStyle(color),
            h2 = MaterialTheme.typography.headlineLarge.toHtmlTextStyle(color),
            h3 = MaterialTheme.typography.headlineMedium.toHtmlTextStyle(color),
            h4 = MaterialTheme.typography.headlineSmall.toHtmlTextStyle(color),
            h5 = MaterialTheme.typography.titleLarge.toHtmlTextStyle(color),
            body = MaterialTheme.typography.bodyLarge.toHtmlTextStyle(color),
            a = MaterialTheme.typography.bodyLarge.toHtmlTextStyle(color = MaterialTheme.colorScheme.primary),
            table = HtmlText2TableStyle(
                tableCornerRadius = 16.dp,
                tableContentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                headerFontSize = MaterialTheme.typography.titleMedium.fontSize,
                headerBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                bodyFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                bodyBackgroundColor = Color.Transparent,
                textAlignment = TextAlign.Center,
                dividerColor = MaterialTheme.colorScheme.outlineVariant
            )
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
    fun asCss(density: Density, layoutDirection: LayoutDirection): String
}

@Immutable
data class HtmlText2TextStyle(
    val fontSize: TextUnit,
    val textColor: Color,
    val fontWeight: FontWeight,
) : CssRepresentable {

    @Language("CSS")
    override fun asCss(density: Density, layoutDirection: LayoutDirection): String {
        return """
            color: ${textColor.toCss()};
            font-weight: ${fontWeight.weight};
            font-size: ${fontSize.toCss()};
        """.trimIndent()
    }
}

@Immutable
data class HtmlText2TableStyle(
    val tableCornerRadius: Dp,
    val tableContentPadding: PaddingValues,
    val headerFontSize: TextUnit,
    val headerBackgroundColor: Color,
    val bodyFontSize: TextUnit,
    val bodyBackgroundColor: Color,
    val textAlignment: TextAlign,
    val dividerColor: Color
): CssRepresentable {

    @Language("CSS")
    override fun asCss(density: Density, layoutDirection: LayoutDirection): String {
        val cornerRadius = tableCornerRadius.toCssPx()
        return """
            table {
              border: 0;
              border-spacing: 0;
              font-size: ${bodyFontSize.toCss()};
              background-color: ${bodyBackgroundColor.toCss()};
            }

            table tr:first-child {
            	background-color: ${headerBackgroundColor.toCss()};
                font-size: ${headerFontSize.toCss()};
            }

            table td {
            	padding: ${tableContentPadding.toCss(layoutDirection)};
                text-align: ${textAlignment.toString().lowercase()};
            }

            table tr td {
            	border: 1px solid ${dividerColor.toCss()};
            }

            table tr:not(:last-child) td {
            	border-bottom: 0;
            }

            table tr td:not(:last-child) {
            	border-right: 0;
            }

            table tr:first-child td:first-child {
            	border-top-left-radius: ${cornerRadius};
            }

            table tr:first-child td:last-child {
            	border-top-right-radius: ${cornerRadius};
            }

            table tr:last-child td:first-child {
            	border-bottom-left-radius: ${cornerRadius};
            }
            
            table tr:last-child td:last-child {
            	border-bottom-right-radius: ${cornerRadius};
            }
        """.trimIndent()
    }
}

@Immutable
data class HtmlText2ContentStyle(
    val h1: HtmlText2TextStyle,
    val h2: HtmlText2TextStyle,
    val h3: HtmlText2TextStyle,
    val h4: HtmlText2TextStyle,
    val h5: HtmlText2TextStyle,
    val body: HtmlText2TextStyle,
    val a: HtmlText2TextStyle,
    val table: HtmlText2TableStyle
) : CssRepresentable {

    @Language("CSS")
    override fun asCss(density: Density, layoutDirection: LayoutDirection): String {
        return """
            * {
                margin: 0; 
                padding: 0;
            }
            h1 {
                ${h1.asCss(density, layoutDirection)}
            }
            h2 {
                ${h2.asCss(density, layoutDirection)}
            }
            h3 {
                ${h3.asCss(density, layoutDirection)}
            }
            h4 {
                ${h4.asCss(density, layoutDirection)}
            }
            h5 {
                ${h5.asCss(density, layoutDirection)}
            }
            p, body {
                ${body.asCss(density, layoutDirection)}
            }
            a {
                ${a.asCss(density, layoutDirection)}
            }
            ${table.asCss(density, layoutDirection)}
        """.trimIndent()
    }

}

private fun Color.toCss(): String {
    return "rgba(${red * 255f}, ${green * 255f}, ${blue * 255f}, $alpha)"
}

private fun TextUnit.toCss(): String {
    return value.toString() + if (isEm) "em" else "px"
}

private fun Dp.toCssPx(): String {
    return value.toString() + "px"
}

private fun PaddingValues.toCss(layoutDirection: LayoutDirection): String {
    val topPadding = calculateTopPadding().toCssPx()
    val rightPadding = calculateRightPadding(layoutDirection).toCssPx()
    val bottomPadding = calculateBottomPadding().toCssPx()
    val leftPadding = calculateLeftPadding(layoutDirection).toCssPx()
    return "$topPadding $rightPadding $bottomPadding $leftPadding"
}