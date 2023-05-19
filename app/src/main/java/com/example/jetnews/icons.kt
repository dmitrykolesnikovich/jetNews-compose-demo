package com.example.jetnews

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics

@Composable
fun FavoriteButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(imageVector = Icons.Filled.ThumbUpOffAlt, contentDescription = stringResource(R.string.cd_add_to_favorites))
    }
}

@Composable
fun BookmarkButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val clickLabel: String = stringResource(if (isFavorite) R.string.unbookmark else R.string.bookmark)
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        },
        content = {
            Icon(imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, contentDescription = null)
        }
    )
}

@Composable
fun ShareButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(imageVector = Icons.Filled.Share, contentDescription = stringResource(R.string.cd_share))
    }
}

@Composable
fun TextSettingsButton(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(painter = painterResource(R.drawable.ic_text_settings), contentDescription = stringResource(R.string.cd_text_settings))
    }
}
