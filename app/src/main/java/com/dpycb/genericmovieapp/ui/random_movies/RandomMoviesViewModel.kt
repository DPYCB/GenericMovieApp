package com.dpycb.genericmovieapp.ui.random_movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.repo.NetworkState
import io.reactivex.disposables.CompositeDisposable

class RandomMoviesViewModel(private val randomMovieListRepo: RandomMovieListRepo): ViewModel() {
    private val compositeDisposable = CompositeDisposable()


    val moviePagedList: LiveData<PagedList<Movie>> by lazy {
        randomMovieListRepo.fetchMoviePagedList(compositeDisposable)
    }


    val networkState: LiveData<NetworkState> by lazy {
        randomMovieListRepo.getNetworkState()
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}