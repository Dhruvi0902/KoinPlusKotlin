package com.example.koinplusmvvmdemo.domainlayer.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.koinplusmvvmdemo.domainlayer.DataRequest
import com.example.koinplusmvvmdemo.domainlayer.datasources.database.DatabaseModule
import com.example.koinplusmvvmdemo.domainlayer.datasources.network.ApiUrls
import com.example.koinplusmvvmdemo.domainlayer.datasources.network.NetworkOperationObserver
import com.example.koinplusmvvmdemo.domainlayer.managers.DataManager
import com.example.koinplusmvvmdemo.domainlayer.managers.FetchLimiter
import com.example.koinplusmvvmdemo.domainlayer.models.Movie
import com.example.koinplusmvvmdemo.domainlayer.models.Response
import com.example.koinplusmvvmdemo.framework.TaskExecutors
import com.example.koinplusmvvmdemo.framework.dicomponents.MovieListApi
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

/*
 * This class acts as a mediator between database, network and any frontend component.*/
class MovieRepository:KoinComponent {

    val taskExecutors: TaskExecutors by inject()
    val movieListApi: MovieListApi by inject()
    val movieDatabase: DatabaseModule.MovieDatabase by inject()

    private val movieListFetchLimiter: FetchLimiter<String> = FetchLimiter<String>(10, TimeUnit.MINUTES)

    fun loadMovie(movieId: String): LiveData<DataRequest<Movie>> {
        return object : DataManager<Response, Movie>(taskExecutors) {
            override fun loadFromDatabase(): LiveData<Movie> {
                return movieDatabase.movieDao().getMovie(movieId)
            }

            override fun loadFromNetwork(): LiveData<Response>? {
                return null
            }

            override fun shouldFetchData(data: Movie?): Boolean {
                return false
            }

            override fun saveDataToDatabase(data: Movie) {

            }

            override fun processResponse(response: Response): Movie? {
                return null
            }

            override fun clearPreviousData() {

            }
        }.toLiveData()
    }


    fun loadMovieList(
        searchString: String,
        forceRefresh: Boolean
    ): LiveData<DataRequest<List<Movie>>> {
        return object : DataManager<DataRequest<Response>, List<Movie>>(taskExecutors) {
            override fun loadFromDatabase(): LiveData<List<Movie>> {
                return movieDatabase.movieDao().movieList
            }

            @SuppressLint("CheckResult")
            override fun loadFromNetwork(): LiveData<DataRequest<Response>> {
                val responseFromNetwork = MutableLiveData<DataRequest<Response>>()
                movieListApi.searchMovie(ApiUrls.apiToken, searchString, "movie")
                    .subscribeOn(Schedulers.from(taskExecutors.networkOperationThread))
                    .observeOn(Schedulers.from(taskExecutors.mainThread))
                    .subscribe(NetworkOperationObserver(responseFromNetwork, this))
                return responseFromNetwork
            }

            override fun processResponse(dataRequest: DataRequest<Response>): List<Movie>? {
                return if (dataRequest.data == null) null else dataRequest.data.movies
            }

            override fun shouldFetchData(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty() || this@MovieRepository.movieListFetchLimiter.shouldFetch(
                    searchString
                ) || forceRefresh
            }

            override fun saveDataToDatabase(data: List<Movie>) {
                movieDatabase.movieDao().insert(data)
            }

            override fun clearPreviousData() {
                movieDatabase.movieDao().removeAllMovies()
            }
        }.toLiveData()
    }
}
