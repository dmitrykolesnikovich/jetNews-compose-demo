package com.example.jetnews

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.article.ArticleScreen
import com.example.jetnews.ui.article.PostLayout
import com.example.jetnews.ui.home.HomeFeedScreen
import com.example.jetnews.ui.home.HomeFeedWithArticleDetailsScreen
import com.example.jetnews.ui.home.HomeState
import com.example.jetnews.ui.home.PostCardHistory
import com.example.jetnews.ui.home.PostCardPopular
import com.example.jetnews.ui.home.PostCardSimple
import com.example.jetnews.ui.home.PostCardTop
import com.example.jetnews.ui.interests.InterestsScreen
import com.example.jetnews.ui.interests.SelectTopicButton
import com.example.jetnews.ui.interests.TabWithSections
import com.example.jetnews.ui.interests.TabWithTopics
import kotlinx.coroutines.runBlocking

@Preview("Interests screen", "Interests")
@Preview("Interests screen (dark)", "Interests", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Interests screen (big font)", "Interests", fontScale = 1.5f)
@Composable
fun PreviewInterestsScreenDrawer() = AppTheme {
    val tabs: List<Tab> = getPreviewTabs()
    val (currentSection, updateSection) = rememberSaveable { mutableStateOf(tabs.first().section) }

    InterestsScreen(tabs = tabs, currentSection = currentSection, isScreenExpanded = false, selectSection = updateSection, openDrawer = {})
}

@Preview("Interests screen navrail", "Interests", device = Devices.PIXEL_C)
@Preview("Interests screen navrail (dark)", "Interests", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview("Interests screen navrail (big font)", "Interests", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewInterestsScreenNavRail() = AppTheme {
    val tabs: List<Tab> = getPreviewTabs()
    val (currentSection, updateSection) = rememberSaveable { mutableStateOf(tabs.first().section) }

    InterestsScreen(tabs = tabs, currentSection = currentSection, isScreenExpanded = true, selectSection = updateSection, openDrawer = {})
}

@Preview("Interests screen topics tab", "Topics")
@Preview("Interests screen topics tab (dark)", "Topics", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopicsTab() = AppTheme {
    Surface {
        TabWithSections(sections = runBlocking { (InterestsRepositoryImpl().getTopics() as Result.Success).data })
    }
}

@Preview("Interests screen people tab", "People")
@Preview("Interests screen people tab (dark)", "People", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPeopleTab() = AppTheme {
    Surface {
        TabWithTopics(topics = runBlocking { (InterestsRepositoryImpl().getPeople() as Result.Success).data })
    }
}

@Preview("Interests screen publications tab", "Publications")
@Preview("Interests screen publications tab (dark)", "Publications", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPublicationsTab() = AppTheme {
    Surface {
        TabWithTopics(topics = runBlocking { (InterestsRepositoryImpl().getPublications() as Result.Success).data })
    }
}

private fun getPreviewTabs(): List<Tab> {
    val repository: InterestsRepository = InterestsRepositoryImpl()
    val topicsTab: Tab = Tab(Section.Topics) {
        TabWithSections(sections = runBlocking { (repository.getTopics() as Result.Success).data })
    }
    val peopleSection: Tab = Tab(Section.People) {
        TabWithTopics(topics = runBlocking { (repository.getPeople() as Result.Success).data })
    }
    val publicationsTab: Tab = Tab(Section.Publications) {
        TabWithTopics(topics = runBlocking { (repository.getPublications() as Result.Success).data })
    }
    return listOf(topicsTab, peopleSection, publicationsTab)
}

/*navigation*/

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRail() = AppTheme {
    AppNavigationRail(context = EmptyMainContext(), currentRoute = "home")
}

/*home*/

@Preview("Home list drawer screen")
@Preview("Home list drawer screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Home list drawer screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewHomeListDrawerScreen() {
    val postsFeed: PostFeed = runBlocking { (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data }
    AppTheme {
        HomeFeedScreen(
            state = HomeState.WithPosts(
                postFeed = postsFeed,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
                selectedPost = postsFeed.highlighted
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            changeSearchInput = {}
        )
    }
}

@Preview("Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview("Home list navrail screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.NEXUS_7_2013)
@Preview("Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    AppTheme {
        HomeFeedScreen(
            state = HomeState.WithPosts(
                postFeed = postsFeed,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
                selectedPost = postsFeed.highlighted
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            changeSearchInput = {}
        )
    }
}

@Preview("Home list detail screen", device = Devices.PIXEL_C)
@Preview("Home list detail screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview("Home list detail screen (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewHomeListDetailScreen() {
    val postsFeed: PostFeed = runBlocking { (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data }
    AppTheme {
        HomeFeedWithArticleDetailsScreen(
            state = HomeState.WithPosts(
                postFeed = postsFeed,
                isArticleOpen = false,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
                selectedPost = postsFeed.highlighted
            ),
            showTopBar = true,
            toggleFavourite = {},
            selectPost = {},
            updatePosts = {},
            dismissError = {},
            interactedWithFeed = {},
            interactedWithArticleDetails = {},
            openDrawer = {},
            listState = rememberLazyListState(),
            articleDetailLazyListStates = postsFeed.allPosts.associate { post ->
                key(post.id) {
                    post.id to rememberLazyListState()
                }
            },
            hostState = SnackbarHostState(),
            changeSearchInput = {}
        )
    }
}

/*post card*/

@Preview
@Composable
fun PostCardTopPreview() = AppTheme {
    Surface {
        PostCardTop(posts.highlighted)
    }
}

// https://d.android.com/jetpack/compose/tooling#preview-multipreview
@CompletePreviews
@Composable
fun PostCardTopPreviews() = AppTheme {
    Surface {
        PostCardTop(posts.highlighted)
    }
}

@Preview("Regular colors")
@Preview("Dark colors", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPostCardPopular(@PreviewParameter(PostPreviewParameterProvider::class, limit = 1) post: Post) = AppTheme {
    Surface {
        PostCardPopular(post, {})
    }
}

@Preview("Regular colors, long text")
@Composable
fun PreviewPostCardPopularLongText(@PreviewParameter(PostPreviewParameterProvider::class, limit = 1) post: Post) = AppTheme {
    Surface {
        PostCardPopular(
            post = post.copy(
                title = "Title$LOREM_IPSUM",
                metadata = post.metadata.copy(author = Author("Author: $LOREM_IPSUM"), readTimeMinutes = Int.MAX_VALUE)
            ),
            navigateToPost = {}
        )
    }
}

private class PostPreviewParameterProvider : PreviewParameterProvider<Post> {
    override val values: Sequence<Post> = sequenceOf(post1, post2, post3, post4, post5)
}

private const val LOREM_IPSUM: String = """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras ullamcorper pharetra massa,
sed suscipit nunc mollis in. Sed tincidunt orci lacus, vel ullamcorper nibh congue quis.
Etiam imperdiet facilisis ligula id facilisis. Suspendisse potenti. Cras vehicula neque sed
nulla auctor scelerisque. Vestibulum at congue risus, vel aliquet eros. In arcu mauris,
facilisis eget magna quis, rhoncus volutpat mi. Phasellus vel sollicitudin quam, eu
consectetur dolor. Proin lobortis venenatis sem, in vestibulum est. Duis ac nibh interdum,
"""

/*select topic*/

@Preview("Off")
@Preview("Off (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOff() = SelectTopicButtonPreviewTemplate(selected = false)

@Preview("On")
@Preview("On (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOn() = SelectTopicButtonPreviewTemplate(selected = true)

@Composable
private fun SelectTopicButtonPreviewTemplate(selected: Boolean) = AppTheme {
    Surface {
        SelectTopicButton(modifier = Modifier.padding(32.dp), selected = selected)
    }
}

/*drawer*/

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() = AppTheme {
    AppDrawer(currentRoute = "home", context = EmptyMainContext(), closeDrawer = {})
}

/*article*/

@Preview("Article screen")
@Preview("Article screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Article screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewArticleDrawer() = AppTheme {
    ArticleScreen(runBlocking { (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data }, false, {}, false, {})
}

@Preview("Article screen navrail", device = Devices.PIXEL_C)
@Preview("Article screen navrail (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview("Article screen navrail (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PreviewArticleNavRail() = AppTheme {
    ArticleScreen(post = runBlocking { (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data }, true, {}, false, {})
}

/*post cards*/

@Preview("Bookmark Button")
@Composable
fun BookmarkButtonPreview() = AppTheme {
    Surface {
        BookmarkButton(isFavorite = false, onClick = {})
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
fun BookmarkButtonBookmarkedPreview() = AppTheme {
    Surface {
        BookmarkButton(isFavorite = true, onClick = {})
    }
}

@Preview("Simple post card")
@Preview("Simple post card (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimplePostPreview() = AppTheme {
    Surface {
        PostCardSimple(post = post3, navigateToPost = {}, isFavorite = false, toggleFavorite = {})
    }
}

@Preview("Post History card")
@Composable
fun HistoryPostPreview() = AppTheme {
    Surface {
        PostCardHistory(post = post3, navigateToPost = {})
    }
}

/*post*/

@Preview("Post content")
@Preview("Post content (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPost() = AppTheme {
    Surface {
        PostLayout(post = post3)
    }
}
