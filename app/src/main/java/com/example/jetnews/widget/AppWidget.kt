package com.example.jetnews.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.example.jetnews.Divider
import com.example.jetnews.MainApplication
import com.example.jetnews.Post
import com.example.jetnews.PostFeed
import com.example.jetnews.PostsRepository
import com.example.jetnews.R
import com.example.jetnews.successOr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val application: MainApplication = context.applicationContext as MainApplication
        val postsRepository: PostsRepository = application.context.postsRepository
        val initialPostsFeed: PostFeed? = withContext(Dispatchers.IO) { postsRepository.getPostsFeed().successOr(null) }
        val initialBookmarks: Set<String> = withContext(Dispatchers.IO) { postsRepository.observeFavorites().first() }

        provideContent {
            val coroutineScope: CoroutineScope = rememberCoroutineScope()
            val bookmarks: Set<String> by postsRepository.observeFavorites().collectAsState(initialBookmarks)
            val postsFeed: PostFeed? by postsRepository.observePostsFeed().collectAsState(initialPostsFeed)
            val highlightedAndRecommended: List<Post> = postsFeed?.let { listOf(it.highlighted) + it.recommended } ?: emptyList()

            // Provide a custom color scheme if the SDK version doesn't support dynamic colors.
            GlanceTheme(
                colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors else AppWidgetTheme.colors,
                content = {
                    AppWidgetLayout(
                        posts = highlightedAndRecommended,
                        bookmarks = bookmarks,
                        toggleBookmark = { postId ->
                            coroutineScope.launch {
                                postsRepository.toggleFavorite(postId)
                            }
                        }
                    )
                }
            )
        }
    }

    @Composable
    private fun AppWidgetLayout(posts: List<Post>, bookmarks: Set<String>?, toggleBookmark: (String) -> Unit) {
        Column(GlanceModifier.background(GlanceTheme.colors.surface).cornerRadius(24.dp)) {
            Header(GlanceModifier.fillMaxWidth())
            // Set key for each size so that the `toggleBookmark` lambda is called only once for the active size
            key(LocalSize.current) {
                Body(
                    modifier = GlanceModifier.fillMaxWidth(),
                    posts = posts,
                    bookmarks = bookmarks ?: setOf(),
                    toggleBookmark = toggleBookmark
                )
            }
        }
    }

    @Composable
    fun Header(modifier: GlanceModifier) {
        val context: Context = LocalContext.current
        Row(modifier.padding(horizontal = 10.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                provider = ImageProvider(R.drawable.ic_jetnews_logo),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Image(
                contentDescription = context.getString(R.string.app_name),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                provider = ImageProvider(R.drawable.ic_jetnews_wordmark)
            )
        }
    }

    @Composable
    fun Body(modifier: GlanceModifier, posts: List<Post>, bookmarks: Set<String>, toggleBookmark: (String) -> Unit) {
        val postLayoutType: PostLayoutType = LocalSize.current.toPostLayout()
        LazyColumn(modifier = modifier.background(GlanceTheme.colors.background)) {
            itemsIndexed(posts) { index, post ->
                Column(GlanceModifier.padding(horizontal = 14.dp)) {
                    PostLayout(post, bookmarks, toggleBookmark, postLayoutType, modifier = GlanceModifier.fillMaxWidth().padding(15.dp))
                    if (index < posts.lastIndex) {
                        Divider()
                    }
                }
            }
        }
    }

}
