package com.george.mvvmnewsapp.repository

import com.george.mvvmnewsapp.api.RetrofitInstance
import com.george.mvvmnewsapp.dp.ArticleDatabase
import com.george.mvvmnewsapp.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    // our architecture will be that we have that news repository
    // the purpose of Repository is => to get the data form our Database and our remote data source from retrofit from our api

    // in this Repository class we will have a function that directly queries our API for the BreakingNews
    // and in the NewsViewModel we will have that instance of our that Repository
    // so from the ViewModel we will call the functions from our news repository and we also handle the responses of our requests from our news reposetory and then we will have a live data object that will notifiy all of all of our fragments about changes regarding these requests

    // function to get news from our API
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)


    // in here we get the database functions
    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    suspend fun delete(article: Article) =
        db.getArticleDao().deleteArticle(article)

    fun getSavedNews() =
        db.getArticleDao().getAllArticle()
}


/* the porpous of Repository is :
 * to get the data from (((( our data base |||| our remote data source -> from retrofit -> from our api )))) */