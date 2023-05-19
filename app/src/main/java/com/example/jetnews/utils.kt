package com.example.jetnews

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.unit.ColorProvider
import com.example.jetnews.widget.AppWidgetTheme
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

const val applicationUri: String = "https://developer.android.com/jetnews"

val tabContainerModifier: Modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)

val defaultSpacerSize: Dp = 16.dp

data class ErrorMessage(val id: Long, @StringRes val messageId: Int)

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

// Determine the content padding to apply to the different screens of the app
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp, excludeTop: Boolean = false): PaddingValues {
    return WindowInsets.systemBars
        .only(if (excludeTop) WindowInsetsSides.Bottom else WindowInsetsSides.Vertical)
        .add(WindowInsets(top = additionalTop))
        .asPaddingValues()
}

val LazyListState.isScrolled: Boolean
    get() = derivedStateOf { firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0 }.value

internal fun <E> Set<E>.addOrRemove(element: E): Set<E> {
    return this.toMutableSet().apply {
        if (!add(element)) {
            remove(element)
        }
    }.toSet()
}

// https://d.android.com/jetpack/compose/tooling#preview-multipreview

// extra small and extra large font size
@Preview(name = "small font", group = "font scales", fontScale = 0.5f)
@Preview(name = "large font", group = "font scales", fontScale = 1.5f)
annotation class FontScalePreviews

// various device sizes: phone, foldable, and tablet.
@Preview(name = "phone", group = "devices", device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Preview(name = "foldable", group = "devices", device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480")
@Preview(name = "tablet", group = "devices", device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480")
annotation class DevicePreviews

// various common configurations: Dark theme, Small and large font size, various device sizes
@Preview(name = "dark theme", group = "themes", uiMode = Configuration.UI_MODE_NIGHT_YES)
@FontScalePreviews
@DevicePreviews
annotation class CompletePreviews

// drawer state to pass to the modal drawer.
@Composable
fun rememberSizeAwareDrawerState(isScreenExpanded: Boolean): DrawerState {
    val state: DrawerState = rememberDrawerState(DrawerValue.Closed)
    return if (!isScreenExpanded) {
        state // if we want to allow showing the drawer, we use a real, remembered drawer state defined above
    } else {
        DrawerState(DrawerValue.Closed)
    }
}

fun sharePost(post: Post, context: Context) {
    val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.article_share_post)))
}

fun Modifier.interceptKey(key: Key, onKeyEvent: () -> Unit): Modifier {
    return onPreviewKeyEvent { keyEvent: KeyEvent ->
        if (keyEvent.key == key && keyEvent.type == KeyEventType.KeyUp) { // fire onKeyEvent on KeyUp to prevent duplicates
            onKeyEvent()
            true
        } else {
            keyEvent.key == key // only pass the key event to children if keyEvent's not the chosen key
        }
    }
}

fun Modifier.notifyInput(block: () -> Unit): Modifier = composed {
    val blockState: State<() -> Unit> = rememberUpdatedState(block)
    pointerInput(Unit) {
        while (currentCoroutineContext().isActive) {
            awaitPointerEventScope {
                awaitPointerEvent(PointerEventPass.Initial)
                blockState.value()
            }
        }
    }
}

@Composable
fun ProgressIndicator() {
    Box(Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        CircularProgressIndicator()
    }
}

@Composable
fun Divider(thickness: Dp = 1.dp, color: ColorProvider = AppWidgetTheme.outlineVariant) {
    Spacer(GlanceModifier.fillMaxWidth().height(thickness).background(color))
}

fun Context.authorReadTimeString(author: String, readTimeMinutes: Int): String {
    return getString(R.string.home_post_min_read).format(author, readTimeMinutes)
}

@Composable
fun FunctionalityNotAvailablePopup(dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        text = {
            Text(stringResource(R.string.article_functionality_not_available), style = MaterialTheme.typography.bodyLarge)
        },
        confirmButton = {
            TextButton(onClick = dismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
