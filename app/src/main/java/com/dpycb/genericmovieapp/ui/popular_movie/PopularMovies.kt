package com.dpycb.genericmovieapp.ui.popular_movie

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpycb.genericmovieapp.R
import com.dpycb.genericmovieapp.data.api.MovieDBClient
import com.dpycb.genericmovieapp.data.repo.NetworkState
import kotlinx.android.synthetic.main.activity_popular_movies.*
import java.util.prefs.Preferences

class PopularMovies : Fragment() {

    private lateinit var viewModel: PopularMovieViewModel
    lateinit var moviesRepo: MoviePopularListRepo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_popular_movies, container, false)
        val api = MovieDBClient.getClient()
        moviesRepo = MoviePopularListRepo(api)

        viewModel = getViewModel()
        initView(rootView)
        return rootView
    }

    private fun getViewModel(): PopularMovieViewModel {
        return ViewModelProviders.of(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PopularMovieViewModel(
                    moviesRepo) as T
            }
        })[PopularMovieViewModel::class.java]
    }

    private fun initView(rootView: View) {
        val adapter = PopularMoviePagedListAdapter(requireActivity())
        val layoutManager = GridLayoutManager(requireActivity(), 3)

        //next we need to identify if we are showing network status or movies. If we are showing network status, tha it has to occupy all 3 slots
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == adapter.MOVIE_TYPE) return 1
                else return 3
            }
        }

        val recyclerMovieList = rootView.findViewById<RecyclerView>(R.id.recycler_movie_list)
        recyclerMovieList.layoutManager = layoutManager
        recyclerMovieList.setHasFixedSize(true)
        recyclerMovieList.adapter = adapter

        viewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            progress_bar.visibility = if (viewModel.isEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (viewModel.isEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!viewModel.isEmpty()) {
                adapter.setNetworkState(it)
            }
        })
    }
}
