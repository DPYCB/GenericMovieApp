package com.dpycb.genericmovieapp.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.repo.NetworkState
import io.reactivex.disposables.CompositeDisposable

class PopularMovieViewModel(private val moviePageListRepo: MoviePopularListRepo): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val moviePagedList: LiveData<PagedList<Movie>> by lazy {
        moviePageListRepo.fetchMoviePagedList(compositeDisposable)
    }

    val networkState: LiveData<NetworkState> by lazy {
        moviePageListRepo.getNetworkState()
    }

    fun isEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}