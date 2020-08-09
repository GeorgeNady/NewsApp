package com.george.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.george.mvvmnewsapp.NewsApplication
import com.george.mvvmnewsapp.models.Article
import com.george.mvvmnewsapp.models.NewsResponse
import com.george.mvvmnewsapp.repository.NewsRepository
import com.george.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

/* !! as you may be know we cannot use constructor parameters by default for our own view model
// !! if you want to do that & we really need that here because we need our news repository
// !! we need to create what is called "ViewModelProviderFactory" to define how our view model should be created */
class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchingNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("eg")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        // this viewModelScope makes coroutine stay alive as long as our viewModel is alive
        // this is something you should use in News View Model
        // and what we want to do in this function before we make the network call ...
        // we want to emit the loading state to our live data so our fragment wil can handel that

        /* breakingNews.postValue(Resource.Loading()) */
        // in this line we can actually make our network response
        /* val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handelBreakingNewsResponse(response)) */

        safeBreakingNewsCall(countryCode)

    }

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        /* searchingNews.postValue(Resource.Loading())
        val response = newsRepository.searchForNews(searchQuery, searchingNewsPage)
        searchingNews.postValue(handelSearchNewsResponse(response))*/

        safeSearchNewsCall(searchQuery)
    }

    // in this function we can handel the pagination
    // if Successful get success state
    // if Error get error state
    private fun handelBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newsArticles = resultResponse.articles
                    oldArticles?.addAll(newsArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handelSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchingNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newsArticles = resultResponse.articles
                    oldArticles?.addAll(newsArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handelBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchForNews(searchQuery, searchingNewsPage)
                searchingNews.postValue(handelSearchNewsResponse(response))
            } else {
                searchingNews.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchingNews.postValue(Resource.Error("Network Failure"))
                else -> searchingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        // we need to call the connectivity manager
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}