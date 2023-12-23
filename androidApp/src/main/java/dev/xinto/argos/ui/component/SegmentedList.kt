package dev.xinto.argos.ui.component

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    type: SegmentedListType,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    shapes: SegmentedListShapes = SegmentedListDefaults.material3Shapes(),
    tonalElevation: Dp = 3.dp
) {
    ListItem2(
        modifier = modifier,
        onClick = onClick,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shape = shapes.shapeForType(type)
    )
}

@Composable
fun SegmentedListItem(
    modifier: Modifier = Modifier,
    type: SegmentedListType,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    shapes: SegmentedListShapes = SegmentedListDefaults.material3Shapes(),
    tonalElevation: Dp = 3.dp
) {
    ListItem2(
        modifier = modifier,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shape = shapes.shapeForType(type)
    )
}

object SegmentedListDefaults {

    @Composable
    fun material3Shapes(
        onlyShape: CornerBasedShape = MaterialTheme.shapes.medium,
        middleShape: CornerBasedShape = MaterialTheme.shapes.extraSmall,
        firstShape: CornerBasedShape = onlyShape.copy(
            bottomEnd = middleShape.bottomEnd,
            bottomStart = middleShape.bottomStart
        ),
        lastShape: CornerBasedShape = onlyShape.copy(
            topEnd = middleShape.topEnd,
            topStart = middleShape.topStart
        ),
    ): SegmentedListShapes {
        return SegmentedListShapes(
            onlyShape = onlyShape,
            firstShape = firstShape,
            middleShape = middleShape,
            lastShape = lastShape
        )
    }

}

@Immutable
enum class SegmentedListType {
    Only,
    First,
    Middle,
    Last
}

@Immutable
class SegmentedListShapes(
    val onlyShape: Shape,
    val firstShape: Shape,
    val middleShape: Shape,
    val lastShape: Shape
) {

    @Stable
    internal fun shapeForType(type: SegmentedListType): Shape {
        return when (type) {
            SegmentedListType.Only -> onlyShape
            SegmentedListType.First -> firstShape
            SegmentedListType.Middle -> middleShape
            SegmentedListType.Last -> lastShape
        }
    }
}

inline fun LazyListScope.itemsSegmented(
    count: Int,
    noinline key: ((index: Int) -> Any)? = null,
    crossinline contentType: (index: Int) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(type: SegmentedListType, index: Int) -> Unit
) = items(
    count = count,
    key = key,
    contentType = { contentType(it) },
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
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(type: SegmentedListType, item: T) -> Unit
) = itemsSegmented(
    count = items.size,
    key = if (key != null) { index: Int -> key(items[index]) } else null,
    contentType = { index -> contentType(items[index]) }
) { type, i ->
    itemContent(type, items[i])
}

inline fun <T> LazyListScope.itemsSegmented(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline contentType: (item: T) -> Any? = { null },
    crossinline itemContent: @Composable LazyItemScope.(type: SegmentedListType, item: T) -> Unit
) = itemsSegmented(
    count = items.size,
    key = if (key != null) { index: Int -> key(items[index]) } else null,
    contentType = { index -> contentType(items[index]) }
) { type, i ->
    itemContent(type, items[i])
}