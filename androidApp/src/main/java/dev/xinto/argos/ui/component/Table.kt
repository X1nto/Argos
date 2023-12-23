package dev.xinto.argos.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.xinto.argos.ui.theme.ArgosTheme

interface TableScope {

    fun <T> column(
        items: List<T>,
        size: ColumnSize = ColumnSize.Variable(1.0f),
        header: @Composable () -> Unit,
        rowContent: @Composable (T) -> Unit
    )

}

class TableScopeImpl : TableScope {

    data class TableColumn(
        val size: ColumnSize,
        val header: @Composable () -> Unit,
        val rowContent: List<@Composable () -> Unit>
    )

    private val _items = mutableListOf<TableColumn>()
    val items: List<TableColumn> = _items

    override fun <T> column(
        items: List<T>,
        size: ColumnSize,
        header: @Composable () -> Unit,
        rowContent: @Composable (T) -> Unit
    ) {
        _items.add(
            TableColumn(
                size = size,
                header = header,
                rowContent = items.map { { rowContent(it) } }
            )
        )
    }
}

@Immutable
sealed interface ColumnSize {

    @Immutable
    data object Fit : ColumnSize

    @Immutable
    data class Fixed(val width: Dp) : ColumnSize

    @Immutable
    data class Variable(val weight: Float) : ColumnSize
}

@Composable
fun Table(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    border: BorderStroke? = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    content: TableScope.() -> Unit
) {
    val scope = remember { TableScopeImpl().apply(content) }
    val itemCount = remember(scope.items) {
        scope.items[0].rowContent.size
    }
    Row(
        modifier = modifier
            .clip(shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
    ) {
        Column {
            Surface(tonalElevation = 3.dp) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    scope.items.forEachIndexed { index, (size, header, _) ->
                        TableContent(
                            size = size,
                            showDivider = index != 0
                        ) {
                            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                                header()
                            }
                        }
                    }
                }
            }
            repeat(itemCount) {
                HorizontalDivider()
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    scope.items.forEachIndexed { index, (size, _, rowContent) ->
                        TableContent(
                            size = size,
                            showDivider = index != 0
                        ) {
                            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                                rowContent[it]()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TableContent(
    size: ColumnSize,
    showDivider: Boolean,
    content: @Composable () -> Unit
) {
    if (showDivider) {
        VerticalDivider()
    }
    val columnModifier = remember(size) {
        when (size) {
            is ColumnSize.Fit -> Modifier.wrapContentWidth(unbounded = true)
            is ColumnSize.Fixed -> Modifier.width(size.width)
            is ColumnSize.Variable -> Modifier.weight(size.weight)
        }
    }
    Box(
        modifier = Modifier
            .then(columnModifier)
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
@PreviewLightDark
fun Table_Preview() {
    ArgosTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val items1 = remember { listOf("animals.bear", "animals.deer", "animals.raccoon") }
            val items2 = remember { listOf("Bear", "Deer", "Raccoon") }
            Table {
                column(
                    items = items1,
                    size = ColumnSize.Variable(1.0f),
                    header = {
                        Text("Key")
                    }
                ) {
                    Text(it)
                }
                column(
                    items = items2,
                    size = ColumnSize.Fixed(100.dp),
                    header = {
                        Text("Value")
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}