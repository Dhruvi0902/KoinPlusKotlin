package com.example.koinplusmvvmdemo.framework.dicomponents

import com.example.koinplusmvvmdemo.domainlayer.datasources.network.ApiUrls
import com.example.koinplusmvvmdemo.domainlayer.models.Response
import com.example.koinplusmvvmdemo.framework.helpers.NetworkUtil
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File

/*
         * Do caching magic here.*/// read from cache for 1 minute
// tolerate 4-weeks stale
// 10 MiB


interface MovieListApi {
    //http://www.omdbapi.com/?s=inter&apikey=13b629a1&type=movie
    @GET("/")
    fun searchMovie(@Query("apikey") apikey: String, @Query("s") searchString: String, @Query("type") type: String): Observable<Response>
}


val networkModuleDi = module {

   single {
       val REWRITE_CACHE_CONTROL_INTERCEPTOR = Interceptor { chain ->
           val originalResponse = chain.proceed(chain.request())
           if (NetworkUtil.isNetworkConnected(androidApplication())) {
               val maxAge = 60
               return@Interceptor originalResponse.newBuilder().header("Cache-Control", "public, max-age=$maxAge").build()
           } else {
               val maxStale = 60 * 60 * 24 * 28
               return@Interceptor originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=$maxStale").build()
           }
       }

       val httpCacheDirectory = File(androidApplication().cacheDir, "responses")
       val cacheSize = 10 * 1024 * 1024
       val cache = Cache(httpCacheDirectory, cacheSize.toLong())
       val okHttpClient:OkHttpClient= OkHttpClient.Builder().addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR).cache(cache).build()

       Retrofit.Builder()
           .baseUrl(ApiUrls.baseUrl).client(okHttpClient)
           .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl(ApiUrls.baseUrl)
           .build()
   }
    factory {
        (get<Retrofit>()).create<MovieListApi>(MovieListApi::class.java)
    }

}