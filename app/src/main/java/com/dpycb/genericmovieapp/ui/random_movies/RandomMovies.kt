package com.dpycb.genericmovieapp.ui.random_movies

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dpycb.genericmovieapp.R
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.api.MovieDBClient
import com.dpycb.genericmovieapp.data.repo.NetworkState
import com.dpycb.genericmovieapp.ui.popular_movie.PopularMovieViewModel
import kotlinx.android.synthetic.main.activity_random_movies.*

class RandomMovies : Fragment() {
    private lateinit var viewModel: RandomMoviesViewModel
    lateinit var moviesRepo: RandomMovieListRepo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_random_movies, container, false)

        val api = MovieDBClient.getClient()
        moviesRepo = RandomMovieListRepo(api)

        viewModel = getViewModel()

        initView(rootView)
        return rootView
    }

    private fun getViewModel(): RandomMoviesViewModel {
        return ViewModelProviders.of(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RandomMoviesViewModel(
                    moviesRepo) as T
            }
        })[RandomMoviesViewModel::class.java]
    }

    private fun initView(rootView: View) {
        val adapter = RandomMoviesPagedListAdapter(requireActivity())
        val layoutManager = GridLayoutManager(requireActivity(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
        }
        }

        val recyclerMovieList = rootView.findViewById<RecyclerView>(R.id.recycler_movie_list)
        recyclerMovieList.layoutManager = layoutManager
        recyclerMovieList.setHasFixedSize(true)
        recyclerMovieList.adapter = adapter

        viewModel.moviePagedList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })


        val swipeRefresh = rootView.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh?.setOnRefreshListener {
            findNavController().navigate(R.id.randomMovies)
            swipeRefresh.isRefreshing = false
        }
    }


}
