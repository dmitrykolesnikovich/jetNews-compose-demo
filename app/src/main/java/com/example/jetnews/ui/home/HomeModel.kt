package com.example.jetnews.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.ErrorMessage
import com.example.jetnews.HomeScreenType
import com.example.jetnews.Post
import com.example.jetnews.PostFeed
import com.example.jetnews.PostsRepository
import com.example.jetnews.R
import com.example.jetnews.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface HomeState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    data class WithoutPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
    ) : HomeState

    data class WithPosts(
        val postFeed: PostFeed,
        val isArticleOpen: Boolean,
        val favorites: Set<String>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        val selectedPost: Post,
    ) : HomeState

}

private data class HomeStateInternal(
    val postFeed: PostFeed? = null,
    val selectedPostId: String? = null,
    val isArticleOpen: Boolean = false,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
) {

    fun toState(): HomeState {
        if (postFeed == null) {
            return HomeState.WithoutPosts(isLoading, errorMessages, searchInput)
        } else {
            val selectedPost: Post = postFeed.allPosts.find { it.id == selectedPostId } ?: postFeed.highlighted
            return HomeState.WithPosts(postFeed, isArticleOpen, favorites, isLoading, errorMessages, searchInput, selectedPost)
        }
    }

}

class HomeModel(private val repository: PostsRepository, selectedPostId: String?) : ViewModel() {

    private val _flow = MutableStateFlow(HomeStateInternal(isLoading = true, selectedPostId = selectedPostId, isArticleOpen = selectedPostId != null))
    val flow = _flow.map(HomeStateInternal::toState).stateIn(viewModelScope, SharingStarted.Eagerly, _flow.value.toState())

    init {
        updatePosts()

        viewModelScope.launch {
            repository.observeFavorites().collect { favorites ->
                _flow.update { state ->
                    state.copy(favorites = favorites)
                }
            }
        }
    }

    fun updatePosts() {
        _flow.update { state ->
            state.copy(isLoading = true)
        }

        viewModelScope.launch {
            val postFeedResult: Result<PostFeed> = repository.getPostsFeed()
            _flow.update { state ->
                when (postFeedResult) {
                    is Result.Success -> state.copy(postFeed = postFeedResult.data, isLoading = false)
                    is Result.Error -> state.copy(
                        errorMessages = state.errorMessages + ErrorMessage(
                            UUID.randomUUID().mostSignificantBits,
                            R.string.load_error
                        ), isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavourite(postId: String) = viewModelScope.launch {
        repository.toggleFavorite(postId)
    }


    fun selectPost(postId: String) {
        interactedWithArticleDetails(postId)
    }

    fun errorShown(errorId: Long) = _flow.update { state ->
        state.copy(errorMessages = state.errorMessages.filterNot { it.id == errorId })
    }

    fun interactedWithFeed() = _flow.update { state ->
        state.copy(isArticleOpen = false)
    }

    fun interactedWithArticleDetails(postId: String) = _flow.update { state ->
        state.copy(selectedPostId = postId, isArticleOpen = true)
    }

    fun changeSearchInput(searchInput: String) = _flow.update { state ->
        state.copy(searchInput = searchInput)
    }

    companion object {
        fun provideFactory(repository: PostsRepository, selectedPostId: String?): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeModel(repository, selectedPostId) as T
                }
            }
    }

}

/*convenience*/

@Composable
fun HomeState.homeScreenType(isScreenExpanded: Boolean): HomeScreenType {
    if (isScreenExpanded) {
        return HomeScreenType.FeedWithArticleDetails
    } else {
        when (this) {
            is HomeState.WithPosts -> {
                if (isArticleOpen) {
                    return HomeScreenType.ArticleDetails
                } else {
                    return HomeScreenType.Feed
                }
            }

            is HomeState.WithoutPosts -> {
                return HomeScreenType.Feed
            }
        }
    }
}

fun HomeState.allPosts(): List<Post> {
    when (this) {
        is HomeState.WithPosts -> {
            return postFeed.allPosts
        }

        is HomeState.WithoutPosts -> {
            return emptyList()
        }
    }
}
