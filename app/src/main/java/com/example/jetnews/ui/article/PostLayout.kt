package com.example.jetnews.ui.article

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetnews.AppTheme
import com.example.jetnews.Markup
import com.example.jetnews.MarkupType
import com.example.jetnews.Metadata
import com.example.jetnews.Paragraph
import com.example.jetnews.ParagraphStyling
import com.example.jetnews.ParagraphType
import com.example.jetnews.Post
import com.example.jetnews.R
import com.example.jetnews.codeBlockBackground
import com.example.jetnews.defaultSpacerSize

@Composable
fun PostLayout(post: Post, modifier: Modifier = Modifier, listState: LazyListState = rememberLazyListState()) {
    LazyColumn(
        contentPadding = PaddingValues(defaultSpacerSize),
        modifier = modifier,
        state = listState,
        content = {
            postContentItems(post)
        },
    )
}

fun LazyListScope.postContentItems(post: Post) {
    item {
        PostHeaderImage(post)
        Spacer(Modifier.height(defaultSpacerSize))
        Text(post.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        if (post.subtitle != null) {
            Text(post.subtitle, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(defaultSpacerSize))
        }
    }
    item {
        PostMetadataLayout(post.metadata, Modifier.padding(bottom = 24.dp))
    }
    items(post.paragraphs) { paragraph ->
        ParagraphLayout(paragraph)
    }
}

@Composable
private fun PostHeaderImage(post: Post) {
    Image(
        painter = painterResource(post.image),
        contentDescription = null, // decorative
        modifier = Modifier.heightIn(min = 180.dp).fillMaxWidth().clip(shape = MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun PostMetadataLayout(metadata: Metadata, modifier: Modifier = Modifier) {
    Row(modifier.semantics(mergeDescendants = true) {}) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null, // decorative
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(metadata.author.name, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 4.dp))
            Text(stringResource(R.string.article_post_min_read, metadata.date, metadata.readTimeMinutes), style = AppTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ParagraphLayout(paragraph: Paragraph) {
    val (textStyle, paragraphStyle, trailingPadding) = paragraph.type.getTextAndParagraphStyle()

    val text: AnnotatedString = paragraphToAnnotatedString(paragraph, AppTheme.typography, AppTheme.colorScheme().codeBlockBackground)
    Box(modifier = Modifier.padding(bottom = trailingPadding)) {
        when (paragraph.type) {
            ParagraphType.Bullet -> BulletParagraph(text, textStyle, paragraphStyle)
            ParagraphType.CodeBlock -> CodeBlockParagraph(text, textStyle, paragraphStyle)
            ParagraphType.Header -> Text(text, modifier = Modifier.padding(4.dp), style = textStyle.merge(paragraphStyle))
            else -> Text(text, modifier = Modifier.padding(4.dp), style = textStyle)
        }
    }
}

@Composable
private fun CodeBlockParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.codeBlockBackground,
        content = {
            Text(text, modifier = Modifier.padding(16.dp), style = textStyle.merge(paragraphStyle))
        }
    )
}

@Composable
private fun BulletParagraph(text: AnnotatedString, textStyle: TextStyle, paragraphStyle: ParagraphStyle) {
    Row {
        with(LocalDensity.current) {
            Box(modifier = Modifier.size(8.sp.toDp(), 8.sp.toDp()).alignBy { 9.sp.roundToPx() } .background(LocalContentColor.current, CircleShape), content = {})
        }
        Text(text, modifier = Modifier.weight(1f).alignBy(FirstBaseline), style = textStyle.merge(paragraphStyle))
    }
}

@Composable
private fun ParagraphType.getTextAndParagraphStyle(): ParagraphStyling {
    val typography: Typography = MaterialTheme.typography
    var textStyle: TextStyle = typography.bodyLarge
    var paragraphStyle: ParagraphStyle = ParagraphStyle()
    var trailingPadding: Dp = 24.dp

    when (this) {
        ParagraphType.Caption -> {
            textStyle = typography.labelMedium
        }
        ParagraphType.Title -> {
            textStyle = typography.headlineLarge
        }
        ParagraphType.Subhead -> {
            textStyle = typography.headlineSmall
            trailingPadding = 16.dp
        }
        ParagraphType.Text -> {
            textStyle = typography.bodyLarge.copy(lineHeight = 28.sp)
        }
        ParagraphType.Header -> {
            textStyle = typography.headlineMedium
            trailingPadding = 16.dp
        }
        ParagraphType.CodeBlock -> {
            textStyle = typography.bodyLarge.copy(fontFamily = FontFamily.Monospace)
        }
        ParagraphType.Quote -> {
            textStyle = typography.bodyLarge
        }
        ParagraphType.Bullet -> {
            paragraphStyle = ParagraphStyle(textIndent = TextIndent(firstLine = 8.sp))
        }
    }
    return ParagraphStyling(textStyle, paragraphStyle, trailingPadding)
}

private fun paragraphToAnnotatedString(paragraph: Paragraph, typography: Typography, codeBlockBackground: Color): AnnotatedString {
    return AnnotatedString(paragraph.text, spanStyles = paragraph.markups.map { it.toAnnotatedStringItem(typography, codeBlockBackground) })
}

private fun Markup.toAnnotatedStringItem(typography: Typography, codeBlockBackground: Color): AnnotatedString.Range<SpanStyle> = when (type) {
    MarkupType.Italic -> {
        AnnotatedString.Range(typography.bodyLarge.copy(fontStyle = FontStyle.Italic).toSpanStyle(), start, end)
    }
    MarkupType.Link -> {
        AnnotatedString.Range(typography.bodyLarge.copy(textDecoration = TextDecoration.Underline).toSpanStyle(), start, end)
    }
    MarkupType.Bold -> {
        AnnotatedString.Range(typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle(), start, end)
    }
    MarkupType.Code -> {
        AnnotatedString.Range(typography.bodyLarge.copy(background = codeBlockBackground, fontFamily = FontFamily.Monospace).toSpanStyle(), start, end)
    }
}
