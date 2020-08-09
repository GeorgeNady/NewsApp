package com.george.mvvmnewsapp.models

data class NewsResponse(
    val articles: MutableList<Article>,        // the Article class is the data type of the list
    val status: String,
    val totalResults: Int
)