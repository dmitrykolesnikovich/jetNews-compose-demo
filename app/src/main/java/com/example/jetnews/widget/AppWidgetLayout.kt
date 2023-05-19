package com.example.jetnews.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.example.jetnews.Post
import com.example.jetnews.R
import com.example.jetnews.authorReadTimeString
import com.example.jetnews.openPost

enum class PostLayoutType {
    HORIZONTAL_SMALL,
    HORIZONTAL_LARGE,
    VERTICAL
}

fun DpSize.toPostLayout(): PostLayoutType {
    return when {
        (this.width <= 300.dp) -> PostLayoutType.VERTICAL
        (this.width <= 700.dp) -> PostLayoutType.HORIZONTAL_SMALL
        else -> PostLayoutType.HORIZONTAL_LARGE
    }
}

@Composable
fun PostLayout(post: Post, bookmarks: Set<String>, toggleBookmark: (String) -> Unit, type: PostLayoutType, modifier: GlanceModifier) {
    when (type) {
        PostLayoutType.HORIZONTAL_SMALL -> HorizontalPost(post, bookmarks, toggleBookmark, modifier, showThumbnail = true)
        PostLayoutType.HORIZONTAL_LARGE -> HorizontalPost(post, bookmarks, toggleBookmark, modifier, showThumbnail = false)
        PostLayoutType.VERTICAL -> VerticalPost(post, bookmarks, toggleBookmark, modifier)
    }
}

@Composable
fun HorizontalPost(post: Post, bookmarks: Set<String>, toggleBookmark: (String) -> Unit, modifier: GlanceModifier, showThumbnail: Boolean) {
    val context: Context = LocalContext.current
    Row(modifier.clickable(onClick = openPost(post, context)), verticalAlignment = Alignment.Vertical.CenterVertically) {
        if (showThumbnail) {
            PostImage(image = post.imageThumb, modifier = GlanceModifier.size(80.dp), contentScale = ContentScale.Fit)
        } else {
            PostImage(image = post.image, modifier = GlanceModifier.width(250.dp), contentScale = ContentScale.Crop)
        }
        PostDescription(
            title = post.title,
            metadata = context.authorReadTimeString(author = post.metadata.author.name, readTimeMinutes = post.metadata.readTimeMinutes),
            modifier = GlanceModifier.defaultWeight().padding(horizontal = 20.dp)
        )
        BookmarkButton(
            id = post.id,
            isBookmarked = bookmarks.contains(post.id),
            toggleBookmark = toggleBookmark
        )
    }
}

@Composable
fun VerticalPost(post: Post, bookmarks: Set<String>, toggleBookmark: (String) -> Unit, modifier: GlanceModifier) {
    val context: Context = LocalContext.current
    Column(modifier.clickable(onClick = openPost(post, context)), verticalAlignment = Alignment.Vertical.CenterVertically) {
        PostImage(image = post.image, modifier = GlanceModifier.fillMaxWidth())
        Spacer(GlanceModifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PostDescription(
                title = post.title,
                metadata = context.authorReadTimeString(
                    author = post.metadata.author.name,
                    readTimeMinutes = post.metadata.readTimeMinutes
                ),
                modifier = GlanceModifier.defaultWeight()
            )
            Spacer(modifier = GlanceModifier.width(10.dp))
            BookmarkButton(post.id, isBookmarked = bookmarks.contains(post.id), toggleBookmark = toggleBookmark)
        }
    }
}

@Composable
fun BookmarkButton(id: String, isBookmarked: Boolean, toggleBookmark: (String) -> Unit) {
    Image(
        provider = ImageProvider(if (isBookmarked) R.drawable.ic_jetnews_bookmark_filled else R.drawable.ic_jetnews_bookmark),
        colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
        contentDescription = "${if (isBookmarked) R.string.unbookmark else R.string.bookmark}",
        modifier = GlanceModifier.clickable { toggleBookmark(id) }
    )
}

@Composable
fun PostImage(image: Int, modifier: GlanceModifier = GlanceModifier, contentScale: ContentScale = ContentScale.Crop) {
    Image(
        provider = ImageProvider(image),
        contentScale = contentScale,
        contentDescription = null,
        modifier = modifier.cornerRadius(5.dp)
    )
}

@Composable
fun PostDescription(title: String, metadata: String, modifier: GlanceModifier) {
    Column(modifier) {
        Text(title, maxLines = 3, style = AppWidgetTheme.bodyLarge.copy(color = GlanceTheme.colors.onBackground))
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(metadata, style = AppWidgetTheme.bodySmall.copy(color = GlanceTheme.colors.onBackground))
    }
}
