package com.example.koinplusmvvmdemo.clientlayer.presentations.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.koinplusmvvmdemo.domainlayer.DataRequest
import com.example.koinplusmvvmdemo.domainlayer.models.Movie
import com.example.koinplusmvvmdemo.domainlayer.repositories.MovieRepository

class MovieViewModel
constructor(/*
    * In ViewModel we bind Model to Database because we want our app to have single source of
    * information. So basically, we fetch data and store it to database and then update our ui
    * with the data we have in our database.*/

    var movieRepository: MovieRepository
) : ViewModel() {

    /*
     * Whenever value in both of these variable gets change, it will trigger loadMovie and loadMovieList method respectively
     * in Repository, Which would eventually grab data and update the ui.*/

    internal val observableMovieIdString: MutableLiveData<String>

    internal/*
         * Making a change in value triggers request.*/ val observableMovieSearchString: MutableLiveData<String>

    /*
     * We would get data in ui from here.*/
    internal/*
         * Now we got a situation, to observe a live data, observer must be a lifecycle owner.
         * ViewModel is not a lifecycle owner because it doesn't have any lifecycle which a live data needs.
         *
         * Methods that we have in Transformation class make the original lifecycle mechanism available here.
         * Thus, we are able to observe changes at this place and perform operations on that and
         * so our ui capable of having the updated data. */ val observableMovieList: LiveData<DataRequest<List<Movie>>>
        get() = Transformations.switchMap(observableMovieSearchString) { newSearchString ->
            movieRepository.loadMovieList(
                newSearchString,
                false
            )
        }

    internal val observableMovie: LiveData<DataRequest<Movie>>
        get() = Transformations.switchMap(observableMovieIdString) { newMovieId ->
            movieRepository.loadMovie(
                newMovieId
            )
        }

    init {
        observableMovieIdString = MutableLiveData()
        observableMovieSearchString = MutableLiveData()
    }

}
