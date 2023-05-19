package com.example.jetnews.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetnews.HomeScreenType
import com.example.jetnews.ui.article.ArticleScreen

@Composable
fun HomeRoute(model: HomeModel, isScreenExpanded: Boolean, openDrawer: () -> Unit) {
    val state: HomeState by model.state.collectAsStateWithLifecycle()
    val hostState: SnackbarHostState = remember { SnackbarHostState() }

    val listState: LazyListState = rememberLazyListState()
    val articleDetailLazyListStates: Map<String, LazyListState> = state.allPosts().associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }

    val homeScreenType: HomeScreenType = state.homeScreenType(isScreenExpanded)
    when (homeScreenType) {
        HomeScreenType.FeedWithArticleDetails -> {
            HomeFeedWithArticleDetailsScreen(
                state = state,
                showTopBar = !isScreenExpanded,
                toggleFavourite = model::toggleFavourite,
                selectPost = model::selectPost,
                updatePosts = model::updatePosts,
                dismissError = model::errorShown,
                interactedWithFeed = model::interactedWithFeed,
                interactedWithArticleDetails = model::interactedWithArticleDetails,
                openDrawer = openDrawer,
                listState = listState,
                articleDetailLazyListStates = articleDetailLazyListStates,
                hostState = hostState,
                changeSearchInput = model::changeSearchInput,
            )
        }

        HomeScreenType.Feed -> {
            HomeFeedScreen(
                state = state,
                showTopAppBar = !isScreenExpanded,
                onToggleFavorite = model::toggleFavourite,
                onSelectPost = model::selectPost,
                onRefreshPosts = model::updatePosts,
                onErrorDismiss = model::errorShown,
                openDrawer = openDrawer,
                homeListLazyListState = listState,
                snackbarHostState = hostState,
                changeSearchInput = model::changeSearchInput
            )
        }

        HomeScreenType.ArticleDetails -> {
            @Suppress("NAME_SHADOWING")
            val state: HomeState.WithPosts = state as HomeState.WithPosts

            ArticleScreen(
                post = state.selectedPost,
                isScreenExpanded = isScreenExpanded,
                onClickBack = model::interactedWithFeed,
                isFavorite = state.favorites.contains(state.selectedPost.id),
                toggleFavorite = { model.toggleFavourite(state.selectedPost.id) },
                listState = articleDetailLazyListStates.getValue(state.selectedPost.id)
            )

            BackHandler {
                model.interactedWithFeed()
            }
        }
    }
}
