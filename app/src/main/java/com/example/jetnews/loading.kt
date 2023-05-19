package com.example.jetnews

import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun LoadingLayout(isEmpty: Boolean, isLoading: Boolean, load: () -> Unit, empty: @Composable () -> Unit, content: @Composable () -> Unit) {
    if (isEmpty) {
        empty()
    } else {
        @Suppress("DEPRECATION")
        val state: SwipeRefreshState = rememberSwipeRefreshState(isLoading)
        @Suppress("DEPRECATION")
        SwipeRefresh(state, onRefresh = load, content = content)
    }
}
