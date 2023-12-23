package dev.xinto.argos.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.xinto.argos.R
import dev.xinto.argos.ui.theme.ArgosTheme

@Composable
fun ExpandableListItem(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .width(IntrinsicSize.Min)
    ) {
        ListItem2(
            modifier = Modifier.fillMaxWidth(),
            checked = expanded,
            onCheckedChange = onExpandedChange,
            enabled = enabled,
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = {
                val iconRotation by animateFloatAsState(
                    targetValue = if (expanded) 270f else 90f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "ExpandableListItem arrow"
                )
                Icon(
                    modifier = Modifier.graphicsLayer {
                        rotationZ = iconRotation
                    },
                    painter = painterResource(R.drawable.ic_navigate_next),
                    contentDescription = null
                )
            },
            shape = MaterialTheme.shapes.extraSmall
        )
        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                expandedContent()
            }
        }
    }
}

@Composable
@PreviewLightDark
fun ExpandableCard_Preview() {
    ArgosTheme {
        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        ExpandableListItem(
            modifier = Modifier.width(300.dp),
            expanded = expanded,
            onExpandedChange = setExpanded,
            headlineContent = {
                Text("Main content")
            },
            expandedContent = {
                Box(
                    modifier = Modifier.padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Expanded content")
                }
            }
        )
    }
}