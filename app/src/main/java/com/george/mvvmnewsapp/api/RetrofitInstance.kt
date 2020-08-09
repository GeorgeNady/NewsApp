package com.george.mvvmnewsapp.api

import com.george.mvvmnewsapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    // this retrofit singlton class created for:
    // enables us to make request from everywhere in our code

    companion object {

        // this lazy means that we just initialize this block of code /* once */
        private val retrofit by lazy {

            // we will attach this to our retrofite opject
            // to be able to see which request we are actually making
            // and what response are
            val logging = HttpLoggingInterceptor()

            // we need to set the logging level
            // we actually see the body of our response
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // we need above Interceptor to create a Network Client
            val clint = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()


            Retrofit.Builder()
                .baseUrl(BASE_URL)
                // we add the converter factory to our Retrofit
                /*
                used to determine how the response should actually be
                interpreted and converted to kotlin opject
                 */
                // GsonConverterFactory : is the google implementation of json converting
                .addConverterFactory(GsonConverterFactory.create())
                .client(clint)
                .build()
        }

        val api: NewsAPI by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }

}