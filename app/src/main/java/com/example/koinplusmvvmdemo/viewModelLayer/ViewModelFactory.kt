package com.example.koinplusmvvmdemo.viewModelLayer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MovieViewModel
import com.example.koinplusmvvmdemo.domainlayer.repositories.MovieRepository
import io.reactivex.annotations.NonNull

class ViewModelFactory
/*
     * This app had only one view model. So, we just passed repository here.
     * But in production app we would have many repositories for many view models.
     *
     * Therefore we must provide repository from other way.*/
constructor(private val repository: MovieRepository) : ViewModelProvider.Factory {


    @NonNull
    override fun <T : ViewModel> create(
            @NonNull modelClass: Class<T>): T {
        /*
         * Here we can create various view Models.*/
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}