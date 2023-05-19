package com.example.jetnews.ui.article

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.jetnews.AppTopBar
import com.example.jetnews.BookmarkButton
import com.example.jetnews.FavoriteButton
import com.example.jetnews.FunctionalityNotAvailablePopup
import com.example.jetnews.Post
import com.example.jetnews.R
import com.example.jetnews.ShareButton
import com.example.jetnews.TextSettingsButton
import com.example.jetnews.sharePost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    post: Post,
    isScreenExpanded: Boolean,
    onClickBack: () -> Unit,
    isFavorite: Boolean,
    toggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val context: Context = LocalContext.current

    var showUnimplementedActionDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    if (showUnimplementedActionDialog) {
        FunctionalityNotAvailablePopup { showUnimplementedActionDialog = false }
    }

    Row(modifier.fillMaxSize()) {
        ArticleScreenContent(
            post = post,
            // Allow opening the Drawer if the screen is not expanded
            icon = {
                if (!isScreenExpanded) {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            // Show the bottom bar if the screen is not expanded
            bottomBar = {
                if (!isScreenExpanded) {
                    BottomAppBar(
                        actions = {
                            FavoriteButton(onClick = { showUnimplementedActionDialog = true })
                            BookmarkButton(isFavorite = isFavorite, onClick = toggleFavorite)
                            ShareButton(onClick = { sharePost(post, context) })
                            TextSettingsButton(onClick = { showUnimplementedActionDialog = true })
                        }
                    )
                }
            },
            listState = listState
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ArticleScreenContent(post: Post, icon: @Composable () -> Unit, bottomBar: @Composable () -> Unit, listState: LazyListState = rememberLazyListState()) {
    val topBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)

    Scaffold(
        topBar = {
            AppTopBar(post.publication?.name.orEmpty(), icon, scrollBehavior)
        },
        bottomBar = bottomBar,
        content = { paddings ->
            PostLayout(post, Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).padding(paddings), listState)
        }
    )
}
