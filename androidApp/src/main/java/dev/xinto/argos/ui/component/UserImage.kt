package dev.xinto.argos.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import dev.xinto.argos.R

@Composable
fun UserImage(
    modifier: Modifier = Modifier,
    url: String?
) {
    if (url != null) {
        val context = LocalContext.current
        val imageRequest = remember(context) {
            ImageRequest.Builder(context)
                .data(url)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        }
        AsyncImage(
            modifier = modifier.clip(CircleShape),
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_account),
            contentDescription = null
        )
    }
}