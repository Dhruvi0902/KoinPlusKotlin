package com.example.koinplusmvvmdemo.clientlayer.presentations.movie.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MoviePresenter
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MovieViewModel
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.ViewController
import com.example.koinplusmvvmdemo.databinding.FragmentMovieDetailsBinding
import com.example.koinplusmvvmdemo.viewModelLayer.ViewModelFactory

class MovieDetailsFragment : Fragment(), ViewController {

    public lateinit var viewModelFactory: ViewModelFactory

    private var fragmentMovieDetailsBinding: FragmentMovieDetailsBinding? = null

    private lateinit var movieViewModel: MovieViewModel
    private var moviePresenter: MoviePresenter? = null

    override val lifeCycleOwner: LifecycleOwner
        get() = viewLifecycleOwner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMovieDetailsBinding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return if (view != null) view else fragmentMovieDetailsBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviePresenter = MoviePresenter(movieViewModel, this, MoviePresenter.PresenterType.DETAILS)
        fragmentMovieDetailsBinding!!.moviePresenter = moviePresenter
        fragmentMovieDetailsBinding!!.lifecycleOwner = lifeCycleOwner
    }

    override fun onSucceed() {}

    override fun onLoadingOccurred() {

    }

    override fun onErrorOccurred(message: String) {}

    override fun onStart() {
        super.onStart()
        val bundle = arguments
        if (bundle != null)
            moviePresenter!!.movieIdLiveData!!.value = bundle.getString("movie_id")
    }

}
