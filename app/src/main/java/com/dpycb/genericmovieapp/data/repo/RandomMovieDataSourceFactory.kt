package com.dpycb.genericmovieapp.data.repo

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.pojo.Movie
import io.reactivex.disposables.CompositeDisposable


class RandomMovieDataSourceFactory(private val api: IMovieDB, private val compositeDisposable: CompositeDisposable):
    DataSource.Factory<Int, Movie>() {
    val moviesLiveData = MutableLiveData<RandomMovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val moviesDataSource = RandomMovieDataSource(api, compositeDisposable)
        moviesLiveData.postValue(moviesDataSource)
        return moviesDataSource
    }
}