@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jetnews.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetnews.BookmarkButton
import com.example.jetnews.ErrorMessage
import com.example.jetnews.FavoriteButton
import com.example.jetnews.LoadingLayout
import com.example.jetnews.Post
import com.example.jetnews.PostFeed
import com.example.jetnews.ProgressIndicator
import com.example.jetnews.R
import com.example.jetnews.ShareButton
import com.example.jetnews.SnackbarHost
import com.example.jetnews.TextSettingsButton
import com.example.jetnews.interceptKey
import com.example.jetnews.notifyInput
import com.example.jetnews.rememberContentPaddingForScreen
import com.example.jetnews.sharePost
import com.example.jetnews.ui.article.postContentItems

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeFeedWithArticleDetailsScreen(
    state: HomeState,
    showTopBar: Boolean,
    toggleFavourite: (String) -> Unit,
    selectPost: (String) -> Unit,
    updatePosts: () -> Unit,
    dismissError: (Long) -> Unit,
    interactedWithFeed: () -> Unit,
    interactedWithArticleDetails: (String) -> Unit,
    openDrawer: () -> Unit,
    listState: LazyListState,
    articleDetailLazyListStates: Map<String, LazyListState>,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    changeSearchInput: (String) -> Unit,
) {
    val context: Context = LocalContext.current
    HomeScreenWithList(
        state = state,
        showTopBar = showTopBar,
        updatePosts = updatePosts,
        dismissError = dismissError,
        openDrawer = openDrawer,
        hostState = hostState,
        modifier = modifier,
        contentWithPosts = { stateWithPosts, contentModifier ->
            val paddings = rememberContentPaddingForScreen(additionalTop = if (showTopBar) 0.dp else 8.dp, excludeTop = showTopBar)
            Row(contentModifier) {
                PostList(
                    postsFeed = stateWithPosts.postFeed,
                    favorites = stateWithPosts.favorites,
                    showExpandedSearch = !showTopBar,
                    navigateToArticle = selectPost,
                    onToggleFavorite = toggleFavourite,
                    paggings = paddings,
                    modifier = Modifier
                        .width(334.dp)
                        .notifyInput(interactedWithFeed),
                    state = listState,
                    searchInput = stateWithPosts.searchInput,
                    changeSearchInput = changeSearchInput,
                )
                // Crossfade between different detail posts
                Crossfade(targetState = stateWithPosts.selectedPost) { detailPost ->
                    // Get the lazy list state for this detail view
                    val detailLazyListState by remember {
                        derivedStateOf {
                            articleDetailLazyListStates.getValue(detailPost.id)
                        }
                    }

                    // Key against the post id to avoid sharing any state between different posts
                    key(detailPost.id) {
                        LazyColumn(
                            state = detailLazyListState,
                            contentPadding = paddings,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize()
                                .notifyInput { interactedWithArticleDetails(detailPost.id) },
                            content = {
                                stickyHeader {
                                    PostTopBar(
                                        isFavorite = stateWithPosts.favorites.contains(detailPost.id),
                                        toggleFavorite = { toggleFavourite(detailPost.id) },
                                        sharePost = { sharePost(detailPost, context) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(Alignment.End)
                                    )
                                }
                                postContentItems(detailPost)
                            }
                        )
                    }
                }
            }
        },
    )
}


@Composable
fun HomeFeedScreen(
    state: HomeState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    searchInput: String = "",
    changeSearchInput: (String) -> Unit,
) {
    HomeScreenWithList(
        state = state,
        showTopBar = showTopAppBar,
        updatePosts = onRefreshPosts,
        dismissError = onErrorDismiss,
        openDrawer = openDrawer,
        hostState = snackbarHostState,
        modifier = modifier,
        contentWithPosts = { hasPostsUiState, contentModifier ->
            PostList(
                postsFeed = hasPostsUiState.postFeed,
                favorites = hasPostsUiState.favorites,
                showExpandedSearch = !showTopAppBar,
                navigateToArticle = onSelectPost,
                onToggleFavorite = onToggleFavorite,
                paggings = rememberContentPaddingForScreen(
                    additionalTop = if (showTopAppBar) 0.dp else 8.dp,
                    excludeTop = showTopAppBar
                ),
                modifier = contentModifier,
                state = homeListLazyListState,
                searchInput = searchInput,
                changeSearchInput = changeSearchInput
            )
        }
    )
}


@Composable
private fun HomeScreenWithList(
    state: HomeState,
    showTopBar: Boolean,
    updatePosts: () -> Unit,
    dismissError: (Long) -> Unit,
    openDrawer: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    contentWithPosts: @Composable (state: HomeState.WithPosts, modifier: Modifier) -> Unit,
) {
    val topBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState)
        },
        topBar = {
            if (showTopBar) {
                HomeTopAppBar(openDrawer, topBarState)
            }
        },
        modifier = modifier,
        content = { paddings ->
            val contentModifier: Modifier = Modifier.padding(paddings).nestedScroll(scrollBehavior.nestedScrollConnection)

            LoadingLayout(
                isEmpty = when (state) {
                    is HomeState.WithPosts -> false
                    is HomeState.WithoutPosts -> state.isLoading
                },
                isLoading = state.isLoading,
                load = updatePosts,
                empty = {
                    ProgressIndicator()
                },
                content = {
                    when (state) {
                        is HomeState.WithPosts -> contentWithPosts(state, contentModifier)
                        is HomeState.WithoutPosts -> {
                            if (state.errorMessages.isEmpty()) {
                                // if there are no posts, and no error, let the user refresh manually
                                TextButton(
                                    onClick = updatePosts,
                                    modifier = modifier.fillMaxSize(),
                                    content = {
                                        Text(stringResource(R.string.home_tap_to_load_content), textAlign = TextAlign.Center)
                                    }
                                )
                            } else {
                                Box(contentModifier.fillMaxSize(), content = {})
                            }
                        }
                    }
                }
            )
        }
    )

    // Process one error message at a time and show them as Snackbars in the UI
    if (state.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage: ErrorMessage = remember(state) { state.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText: String = stringResource(R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(updatePosts)
        val onErrorDismissState by rememberUpdatedState(dismissError)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or snackbarHostState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, hostState) {
            val snackbarResult: SnackbarResult = hostState.showSnackbar(message = errorMessageText, actionLabel = retryMessageText)
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            onErrorDismissState(errorMessage.id)
        }
    }
}


@Composable
private fun PostList(
    postsFeed: PostFeed,
    favorites: Set<String>,
    showExpandedSearch: Boolean,
    navigateToArticle: (postId: String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    paggings: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    searchInput: String = "",
    changeSearchInput: (String) -> Unit,
) {
    LazyColumn(modifier, state, paggings) {
        if (showExpandedSearch) {
            item {
                HomeSearch(Modifier.padding(horizontal = 16.dp), searchInput = searchInput, changeSearchInput = changeSearchInput)
            }
        }
        item {
            PostListTopSection(postsFeed.highlighted, navigateToArticle)
        }
        if (postsFeed.recommended.isNotEmpty()) {
            item {
                PostListSimpleSection(postsFeed.recommended, navigateToArticle, favorites, onToggleFavorite)
            }
        }
        if (postsFeed.populars.isNotEmpty() && !showExpandedSearch) {
            item {
                PostListPopularSection(postsFeed.populars, navigateToArticle)
            }
        }
        if (postsFeed.recents.isNotEmpty()) {
            item {
                PostListHistorySection(postsFeed.recents, navigateToArticle)
            }
        }
    }
}

@Composable
private fun PostListTopSection(post: Post, navigateToArticle: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium
    )
    PostCardTop(post, modifier = Modifier.clickable(onClick = { navigateToArticle(post.id) }))
    PostListDivider()
}

@Composable
fun PostCardTop(post: Post, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Image(
            painter = painterResource(post.image),
            contentDescription = null, // decorative
            modifier = Modifier
                .heightIn(min = 180.dp)
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        Text(post.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        Text(post.metadata.author.name, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            stringResource(R.string.home_post_min_read, post.metadata.date, post.metadata.readTimeMinutes),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun PostListSimpleSection(
    posts: List<Post>,
    navigateToArticle: (String) -> Unit,
    favorites: Set<String>,
    toggleFavorite: (String) -> Unit
) {
    Column {
        for (post in posts) {
            PostCardSimple(
                post,
                navigateToArticle = navigateToArticle,
                isFavorite = favorites.contains(post.id),
                toggleFavorite = { toggleFavorite(post.id) })
            PostListDivider()
        }
    }
}

@Composable
private fun PostListPopularSection(posts: List<Post>, navigateToArticle: (String) -> Unit) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.home_popular_section_title),
            style = MaterialTheme.typography.titleLarge
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(posts) { post ->
                PostCardPopular(post, navigateToArticle)
            }
        }
        Spacer(Modifier.height(16.dp))
        PostListDivider()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCardPopular(post: Post, navigateToArticle: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = { navigateToArticle(post.id) },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.size(280.dp, 240.dp),
        content = {
            Column {
                Image(
                    painter = painterResource(post.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(post.title, style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        post.metadata.author.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.home_post_min_read, post.metadata.date, post.metadata.readTimeMinutes),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

@Composable
private fun PostListHistorySection(posts: List<Post>, navigateToArticle: (String) -> Unit) {
    Column {
        for (post in posts) {
            PostCardHistory(post, navigateToArticle)
            PostListDivider()
        }
    }
}

@Composable
private fun PostListDivider() {
    Divider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HomeSearch(modifier: Modifier = Modifier, searchInput: String = "", changeSearchInput: (String) -> Unit) {
    val context: Context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current
    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchInput,
        onValueChange = changeSearchInput,
        placeholder = { Text(stringResource(R.string.home_search)) },
        leadingIcon = { Icon(Icons.Filled.Search, null) },
        modifier = modifier
            .fillMaxWidth()
            .interceptKey(Key.Enter) {
                submitSearch(changeSearchInput, context)
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
            },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                submitSearch(changeSearchInput, context)
                keyboardController?.hide()
            }
        )
    )
}

private fun submitSearch(changeSearchInput: (String) -> Unit, context: Context) {
    changeSearchInput("")
    Toast.makeText(context, "Search is not yet implemented", Toast.LENGTH_SHORT).show()
}

@Composable
private fun PostTopBar(isFavorite: Boolean, toggleFavorite: () -> Unit, sharePost: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)),
        modifier = modifier.padding(end = 16.dp),
        content = {
            Row(Modifier.padding(horizontal = 8.dp)) {
                FavoriteButton(onClick = {})
                BookmarkButton(isFavorite = isFavorite, onClick = toggleFavorite)
                ShareButton(onClick = sharePost)
                TextSettingsButton(onClick = {})
            }
        }
    )
}

@Composable
private fun HomeTopAppBar(openDrawer: () -> Unit, topBarState: TopAppBarState, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
    val title: String = stringResource(R.string.app_name)

    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.ic_jetnews_wordmark),
                contentDescription = title,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(R.drawable.ic_jetnews_logo),
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Search is not yet implemented in this configuration", Toast.LENGTH_LONG).show()
                },
                content = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.cd_search))
                })
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
