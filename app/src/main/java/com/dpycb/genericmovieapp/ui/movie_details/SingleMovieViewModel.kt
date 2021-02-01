package com.dpycb.genericmovieapp.ui.movie_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.pojo.MovieDetail
import com.dpycb.genericmovieapp.data.repo.NetworkState
import io.reactivex.disposables.CompositeDisposable

class SingleMovieViewModel(private val movieDetailsRepo: MovieDetailsRepo, movieId: Int): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    //Initializing this value by Lazy because this one is going to be initialized only when requested
    val movieDetails: LiveData<MovieDetail> by lazy {
        movieDetailsRepo.fetchSingleMovieResult(compositeDisposable, movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieDetailsRepo.getMovieDetailNetworkState()
    }

    val similarMovies: LiveData<PagedList<Movie>> by lazy {
        movieDetailsRepo.fetchSimilarMovies(compositeDisposable, movieId)
    }

    fun isEmpty(): Boolean {
        return similarMovies.value?.isEmpty() ?: true
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}