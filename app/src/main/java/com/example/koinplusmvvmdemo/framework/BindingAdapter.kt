package com.example.koinplusmvvmdemo.framework

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.fragments.MovieListFragment
import com.example.koinplusmvvmdemo.domainlayer.models.Movie
import java.util.*


class BindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("list")
        fun setList(view: RecyclerView, movieList: List<Movie>?) {
            if (movieList != null) {
                (Objects.requireNonNull(view.adapter) as MovieListFragment.MovieListAdapter).setMovieList(movieList)
            }
        }

    }
}