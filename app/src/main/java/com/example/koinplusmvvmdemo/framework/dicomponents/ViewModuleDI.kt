package com.example.koinplusmvvmdemo.framework.dicomponents

import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModuledi = module {
    viewModel { MovieViewModel(get()) }

}