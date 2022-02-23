package com.example.koinplusmvvmdemo.clientlayer.presentations.movie.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.koinplusmvvmdemo.R
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MoviePresenter
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.MovieViewModel
import com.example.koinplusmvvmdemo.clientlayer.presentations.movie.ViewController
import com.example.koinplusmvvmdemo.databinding.FragmentMovieListBinding
import com.example.koinplusmvvmdemo.databinding.MovieItemBinding
import com.example.koinplusmvvmdemo.domainlayer.models.Movie
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class MovieListFragment : Fragment(), ViewController {

    val movieViewModel: MovieViewModel by viewModel()
    private var fragmentMovieListBinding: FragmentMovieListBinding? = null
    private val RECYCLER_VIEW_STATE_KEY = "RECYCLER_VIEW_STATE_KEY"
    private var savedInstanceState: Bundle? = null

    override val lifeCycleOwner: LifecycleOwner
        get() = viewLifecycleOwner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMovieListBinding = FragmentMovieListBinding.inflate(inflater, container, false)
        return if (view != null) view else fragmentMovieListBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.savedInstanceState = savedInstanceState
        setUpInitialThings(view)
        val moviePresenter =
            MoviePresenter(movieViewModel, this, MoviePresenter.PresenterType.LIST)
        fragmentMovieListBinding!!.moviePresenter = moviePresenter
        fragmentMovieListBinding!!.lifecycleOwner = lifeCycleOwner
    }

    private fun setUpInitialThings(view: View) {
        val movieListAdapter = object : MovieListAdapter() {
            public override fun onMovieItemClicked(id: String) {
                val bundle = Bundle()
                bundle.putString("movie_id", id)
                Navigation.findNavController(view).navigate(R.id.fragment_movie_details, bundle)
            }
        }
        fragmentMovieListBinding!!.movieListRecyclerView.layoutManager =
            LinearLayoutManager(activity)
        fragmentMovieListBinding!!.movieListRecyclerView.adapter = movieListAdapter
    }

    override fun onPause() {
        super.onPause()
        val bundle = Bundle()
        bundle.putParcelable(
            RECYCLER_VIEW_STATE_KEY,
            Objects.requireNonNull<RecyclerView.LayoutManager>(fragmentMovieListBinding!!.movieListRecyclerView.layoutManager).onSaveInstanceState()
        )
        arguments = bundle
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            RECYCLER_VIEW_STATE_KEY,
            Objects.requireNonNull<RecyclerView.LayoutManager>(fragmentMovieListBinding!!.movieListRecyclerView.layoutManager).onSaveInstanceState()
        )
    }

    override fun onSucceed() {
        savedInstanceState = arguments
        if (savedInstanceState != null) {
            val savedRecyclerLayoutState =
                savedInstanceState!!.getParcelable<Parcelable>(RECYCLER_VIEW_STATE_KEY)
            Objects.requireNonNull<RecyclerView.LayoutManager>(fragmentMovieListBinding!!.movieListRecyclerView.layoutManager)
                .onRestoreInstanceState(savedRecyclerLayoutState)
        }
    }

    override fun onErrorOccurred(message: String) {}

    override fun onLoadingOccurred() {

    }

    abstract inner class MovieListAdapter internal constructor() :
        RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {


        private var movieList: List<Movie>

        init {
            this.movieList = ArrayList()
        }

        internal fun setMovieList(movieList: List<Movie>) {
            this.movieList = movieList
            notifyDataSetChanged()
        }

        internal abstract fun onMovieItemClicked(imdbId: String)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {

            val binding = MovieItemBinding.inflate(layoutInflater, parent, false)
            return MovieListViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
            val movie = movieList[position]
            holder.movieItemBinding.root.setOnClickListener { v -> onMovieItemClicked(movie.imdbID) }
            holder.bind(movie)
        }

        override fun getItemCount(): Int {
            return if (movieList != null) {
                movieList.size
            } else {
                0
            }
        }


        inner class MovieListViewHolder(val movieItemBinding: MovieItemBinding) :
            RecyclerView.ViewHolder(movieItemBinding.root) {

            fun bind(movie: Movie) {
                movieItemBinding.movie = movie
                movieItemBinding.executePendingBindings()
            }
        }
    }

    companion object {

        @BindingAdapter("list")
        fun setList(recyclerView: RecyclerView, movieList: List<Movie>) {
            (Objects.requireNonNull(recyclerView.adapter) as MovieListAdapter).setMovieList(
                movieList
            )
        }
    }
}
