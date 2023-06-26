package com.mobileappconsultant.newsfeedmmsdk.models

data class NewsSource(
    val id: String,
    val name: String,
    val url: String,
    val categories: List<NewsCategory>,
)

data class NewsCategory(
    val id: String,
    val name: String,
    val articles: List<Article>,
)

data class Article(
    val id: String,
    val creators: List<String>,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val link: String,
    val sourceId: String,
    val pubDate: String,
    val content: String,
    val categories: List<String>?,
    val likes: List<String>?,
    val isLiked: Boolean,
)

fun String.toProperImageURL(): String {
    return "https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=${this}&size=256"
}