package com.example.jetnews.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jetnews.Post
import com.example.jetnews.R
import com.example.jetnews.BookmarkButton

@Composable
fun PostCardSimple(post: Post, navigateToArticle: (String) -> Unit, isFavorite: Boolean, toggleFavorite: () -> Unit) {
    val bookmarkAction = stringResource(if (isFavorite) R.string.unbookmark else R.string.bookmark)
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToArticle(post.id) })
            .semantics {
                // By defining a custom action, we tell accessibility services that this whole
                // composable has an action attached to it. The accessibility service can choose
                // how to best communicate this action to the user.
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = bookmarkAction,
                        action = { toggleFavorite(); true }
                    )
                )
            },
        content = {
            PostImage(post, Modifier.padding(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
                content = {
                    PostTitle(post)
                    AuthorAndReadTime(post)
                }
            )
            BookmarkButton(
                isFavorite = isFavorite,
                onClick = toggleFavorite,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .padding(vertical = 2.dp, horizontal = 6.dp) // Remove button semantics so action can be handled at row level
            )
        }
    )
}

@Composable
fun PostCardHistory(post: Post, navigateToArticle: (String) -> Unit) {
    var openDialog: Boolean by remember { mutableStateOf(false) }

    Row(Modifier.clickable(onClick = { navigateToArticle(post.id) })) {
        PostImage(post, Modifier.padding(16.dp))
        Column(
            Modifier.weight(1f).padding(vertical = 12.dp)) {
            Text(stringResource(R.string.home_post_based_on_history), style = MaterialTheme.typography.labelMedium)
            PostTitle(post)
            AuthorAndReadTime(post, Modifier.padding(top = 4.dp))
        }
        IconButton(onClick = { openDialog = true }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(R.string.cd_more_actions))
        }
    }
    if (openDialog) {
        AlertDialog(
            modifier = Modifier.padding(20.dp),
            onDismissRequest = { openDialog = false },
            title = {
                Text(stringResource(R.string.fewer_stories), style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(stringResource(R.string.fewer_stories_content), style = MaterialTheme.typography.bodyLarge)
            },
            confirmButton = {
                Text(
                    text = stringResource(R.string.agree),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(15.dp).clickable { openDialog = false }
                )
            }
        )
    }
}

@Composable
private fun PostImage(post: Post, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(post.imageThumb),
        contentDescription = null, // decorative
        modifier = modifier
            .size(40.dp, 40.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
private fun PostTitle(post: Post) {
    Text(
        text = post.title,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun AuthorAndReadTime(post: Post, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            text = stringResource(R.string.home_post_min_read, post.metadata.author.name, post.metadata.readTimeMinutes),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
