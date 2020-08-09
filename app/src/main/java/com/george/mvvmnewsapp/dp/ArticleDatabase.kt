package com.george.mvvmnewsapp.dp

import android.content.Context
import androidx.room.*
import com.george.mvvmnewsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    // this abstract function ,,,
    // so we don't need to implement it ,,,
    // the implementation of that will happen behind the scenes ,,,
    // Room will do that for us .
    abstract fun getArticleDao() : ArticleDao

    companion object {
        @Volatile // Volatile is mean that other threads  can immediately see when a thread changes this instance
        private var instance: ArticleDatabase? = null
        private val LOCK =  Any()

        // we need to call this invoke whenever we created an instance of the database
        // whenever we write something like /* ArticleDatabase() */
        // this invoke function will be called
        // basically when we initialize or instantiate this object
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            // synchronized(LOCK) : means that everything that happens inside of this block of code here
            // can not be accessed by other threads at the same time
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}