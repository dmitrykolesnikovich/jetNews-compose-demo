package com.example.jetnews

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

enum class HomeScreenType {
    FeedWithArticleDetails,
    Feed,
    ArticleDetails
}

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}

enum class Section(@StringRes val titleResId: Int) {
    Topics(R.string.interests_section_topics),
    People(R.string.interests_section_people),
    Publications(R.string.interests_section_publications)
}

class Tab(val section: Section, val content: @Composable () -> Unit)
data class InterestSection(val title: String, val interests: List<String>)
data class TopicSelection(val section: String, val topic: String)
data class Metadata(val author: Author, val date: String, val readTimeMinutes: Int)
data class Author(val name: String, val url: String? = null)
data class Publication(val name: String, val logoUrl: String)
data class Paragraph(val type: ParagraphType, val text: String, val markups: List<Markup> = emptyList())
data class Markup(val type: MarkupType, val start: Int, val end: Int, val href: String? = null)

data class PostFeed(val highlighted: Post, val recommended: List<Post>, val populars: List<Post>, val recents: List<Post>) {
    val allPosts: List<Post> = listOf(highlighted) + recommended + populars + recents
}

data class Post(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val url: String,
    val publication: Publication? = null,
    val metadata: Metadata,
    val paragraphs: List<Paragraph> = emptyList(),
    @DrawableRes val image: Int,
    @DrawableRes val imageThumb: Int,
)

data class ParagraphStyling(val textStyle: TextStyle, val paragraphStyle: ParagraphStyle, val trailingPadding: Dp)
