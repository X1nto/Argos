package dev.xinto.argos.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ListItem2(
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    shape: Shape = MaterialTheme.shapes.medium,
    tonalElevation: Dp = 3.dp,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        tonalElevation = tonalElevation
    ) {
        ListItem2Content(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent
        )
    }
}

@Composable
fun ListItem2(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    shape: Shape = MaterialTheme.shapes.medium,
    enabled: Boolean = true,
    tonalElevation: Dp = 3.dp,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        enabled = enabled,
        onClick = onClick
    ) {
        ListItem2Content(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent
        )
    }
}

@Composable
fun ListItem2(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    shape: Shape = MaterialTheme.shapes.medium,
    enabled: Boolean = true,
    tonalElevation: Dp = 3.dp,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        enabled = enabled,
        checked = checked,
        onCheckedChange = onCheckedChange
    ) {
        ListItem2Content(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent
        )
    }
}

@Composable
private fun ListItem2Content(
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            Box(modifier = Modifier.padding(start = 4.dp, end = 12.dp)) {
                leadingContent()
            }
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