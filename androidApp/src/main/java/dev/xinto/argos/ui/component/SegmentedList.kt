package dev.xinto.argos.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
enum class SegmentedListType {
    Only,
    First,
    Middle,
    Last
}

@Composable
fun SegmentedListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    type: SegmentedListType,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor)
) {
    val onlyShape = MaterialTheme.shapes.medium
    val middleShape = MaterialTheme.shapes.extraSmall
    val firstShape = onlyShape.copy(bottomEnd = middleShape.bottomEnd, bottomStart = middleShape.bottomStart)
    val lastShape = onlyShape.copy(topEnd = middleShape.topEnd, topStart = middleShape.topStart)
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = when (type) {
            SegmentedListType.Only -> onlyShape
            SegmentedListType.First -> firstShape
            SegmentedListType.Middle -> middleShape
            SegmentedListType.Last -> lastShape
        },
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(12.dp)
                .then(modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingContent != null) {
                leadingContent()
            }
            Column(modifier = Modifier.weight(1f)) {
                if (overlineContent != null) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.labelMedium,
                        content = overlineContent
                    )
                }
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                    content = headlineContent
                )
                if (supportingContent != null) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodySmall,
                        content = supportingContent
                    )
                }
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

fun LazyListScope.itemsSegmented(
    count: Int,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable LazyItemScope.(type: SegmentedListType, index: Int) -> Unit
) = items(
    count = count,
    key = key,
    contentType = contentType,
) {
    val type = remember(it, count) {
        when {
            it == 0 && count == 1 -> SegmentedListType.Only
            it == 0 -> SegmentedListType.First
            it == (count - 1) -> SegmentedListType.Last
            else -> SegmentedListType.Middle
        }
    }
    itemContent(type, it)
}


inline fun <T> LazyListScope.itemsSegmented(
    items: Array<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(type: SegmentedListType, item: T) -> Unit
) = itemsSegmented(
    count = items.size,
    key = if (key != null) { index: Int -> key(index, items[index]) } else null,
    contentType = { index -> contentType(index, items[index]) }
) { type, i ->
    itemContent(type, items[i])
}
inline fun <T> LazyListScope.itemsSegmented(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    crossinline contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    crossinline itemContent: @Composable LazyItemScope.(type: SegmentedListType, item: T) -> Unit
) = itemsSegmented(
    count = items.size,
    key = if (key != null) { index: Int -> key(index, items[index]) } else null,
    contentType = { index -> contentType(index, items[index]) }
) { type, i ->
    itemContent(type, items[i])
}