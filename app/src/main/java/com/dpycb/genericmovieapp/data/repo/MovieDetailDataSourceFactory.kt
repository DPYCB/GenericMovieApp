package com.dpycb.genericmovieapp.data.repo

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.pojo.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieDetailDataSourceFactory(private val api: IMovieDB, private val compositeDisposable: CompositeDisposable, private val movieId: Int):
    DataSource.Factory<Int, Movie>() {
    val similarMoviesLiveData = MutableLiveData<MovieDetailDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val similarMoviesDataSource = MovieDetailDataSource(api, compositeDisposable, movieId)
        similarMoviesLiveData.postValue(similarMoviesDataSource)
        return similarMoviesDataSource
    }
}