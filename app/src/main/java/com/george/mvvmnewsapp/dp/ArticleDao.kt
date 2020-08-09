package com.george.mvvmnewsapp.dp

import androidx.lifecycle.LiveData
import androidx.room.*
import com.george.mvvmnewsapp.models.Article


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticle(): LiveData<List<Article>>
}