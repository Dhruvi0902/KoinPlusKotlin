package com.example.koinplusmvvmdemo.clientlayer.presentations.movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.koinplusmvvmdemo.domainlayer.DataRequest
import com.example.koinplusmvvmdemo.domainlayer.models.Movie

class MoviePresenter(movieViewModel: MovieViewModel, private val viewController: ViewController, private val presenterType: PresenterType) {


    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    /*
     * There are the values which are listened by viewModel to do operations.*/

    /*
     * MovieList is bound to this value.*/
    var movieSearchStringLiveData: MutableLiveData<String>? = null
        private set

    /*
     * MovieDetails is bound to this value.*/
    var movieIdLiveData: MutableLiveData<String>? = null
        private set
    /*
     * These are the values which are listened by UI to render changes.*/

    /*
     * MovieListFragment bound to this value.
     * */
    var movieList: MutableLiveData<List<Movie>>? = null
        private set
    /**
     * MovieDetailsFragment bound to this value.
     */
    var movie: MutableLiveData<Movie>? = null
        private set


    init {
        if (this.presenterType == PresenterType.LIST) {
            movieList = MutableLiveData()
            movieSearchStringLiveData = movieViewModel.observableMovieSearchString
            movieViewModel.observableMovieList.observe(viewController.lifeCycleOwner, Observer<DataRequest<List<Movie>>> { this.consumeResponse(it) })
        } else {
            movie = MutableLiveData()
            movieIdLiveData = movieViewModel.observableMovieIdString
            movieViewModel.observableMovie.observe(viewController.lifeCycleOwner, Observer<DataRequest<Movie>> { this.consumeResponse(it) })
        }
    }


    private fun consumeResponse(
            dataRequest: DataRequest<*>) {
        when (dataRequest.currentState) {
            DataRequest.State.LOADING -> {
                /*
                 * If network operation is going on then we will get the data from
                 * database while the operation is being performed.*/
                isLoading.value = true
                if (presenterType == PresenterType.LIST) {
                    if (dataRequest.data != null) {
                        movieList!!.value = dataRequest.data as List<Movie>
                    }
                } else {
                    if (dataRequest.data != null)
                        movie!!.value = dataRequest.data as Movie
                }
                viewController.onLoadingOccurred()
            }
            DataRequest.State.SUCCEED -> {

                isLoading.value = false
                /*
                 * Retrieve whatever data you expect from here with just one object.*/
                if (presenterType == PresenterType.LIST) {
                    movieList!!.setValue(dataRequest.data as List<Movie>)
                } else {
                    movie!!.setValue(dataRequest.data as Movie)
                }
                viewController.onSucceed()
            }
            DataRequest.State.FAILED -> {
                isLoading.value = false
                viewController.onErrorOccurred("Error")
            }
        }
    }

    enum class PresenterType {
        LIST,
        DETAILS
    }

}
