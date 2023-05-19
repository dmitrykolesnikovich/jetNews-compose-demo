package com.example.jetnews

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

interface PostsRepository {
    suspend fun getPost(postId: String?): Result<Post>
    suspend fun getPostsFeed(): Result<PostFeed>
    fun observeFavorites(): Flow<Set<String>>
    fun observePostsFeed(): Flow<PostFeed?>
    suspend fun toggleFavorite(postId: String)
}

class BlockingFakePostsRepository : PostsRepository {

    private val favoritesFlow: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
    private val postFeedFlow: MutableStateFlow<PostFeed?> = MutableStateFlow(null)

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Unable to find post"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostFeed> {
        postFeedFlow.update { posts }
        return Result.Success(posts)
    }

    override fun observeFavorites(): Flow<Set<String>> {
        return favoritesFlow
    }

    override fun observePostsFeed(): Flow<PostFeed?> {
        return postFeedFlow
    }

    override suspend fun toggleFavorite(postId: String) = favoritesFlow.update { favourites ->
        favourites.addOrRemove(postId)
    }
}

class FakePostsRepository : PostsRepository {

    private val favoritesFlow: MutableStateFlow<Set<String>> = MutableStateFlow(setOf())
    private val postFeedFlow = MutableStateFlow<PostFeed?>(null)

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostFeed> {
        return withContext(Dispatchers.IO) {
            delay(800) // simulate network request

            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                postFeedFlow.update { posts }
                Result.Success(posts)
            }
        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favoritesFlow
    override fun observePostsFeed(): Flow<PostFeed?> = postFeedFlow

    override suspend fun toggleFavorite(postId: String) {
        favoritesFlow.update {
            it.addOrRemove(postId)
        }
    }

    private var requestCount: Int = 0
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0 // every 5 requests fail some loads to simulate a real network.

}

class EmptyPostsRepository : PostsRepository {
    override suspend fun getPost(postId: String?): Result<Post> = error("stub")
    override suspend fun getPostsFeed(): Result<PostFeed> = error("stub")
    override fun observeFavorites(): Flow<Set<String>> = error("stub")
    override fun observePostsFeed(): Flow<PostFeed?> = error("stub")
    override suspend fun toggleFavorite(postId: String) = error("stub")
}