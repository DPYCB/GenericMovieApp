package com.dpycb.genericmovieapp.ui.movie_details

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.api.POSTER_BASE_URL
import com.dpycb.genericmovieapp.data.api.POST_PER_PAGE
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.pojo.MovieDetail
import com.dpycb.genericmovieapp.data.repo.MovieDetailDataSource
import com.dpycb.genericmovieapp.data.repo.MovieDetailDataSourceFactory
import com.dpycb.genericmovieapp.data.repo.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepo(private val api: IMovieDB) {
    private lateinit var movieDetailDataSource: MovieDetailDataSource

    lateinit var movieDetailDataSourceFactory: MovieDetailDataSourceFactory
    lateinit var similarMoviePagedList: LiveData<PagedList<Movie>>

    fun fetchSingleMovieResult(compositeDisposable: CompositeDisposable, movieId: Int): LiveData<MovieDetail> {
        movieDetailDataSource = MovieDetailDataSource(api, compositeDisposable, movieId)
        movieDetailDataSource.fetchMovieDetails()
        return movieDetailDataSource.movieDetailResponse
    }

    fun fetchSimilarMovies(compositeDisposable: CompositeDisposable, movieId: Int): LiveData<PagedList<Movie>> {
        movieDetailDataSourceFactory = MovieDetailDataSourceFactory(api, compositeDisposable, movieId)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        similarMoviePagedList = LivePagedListBuilder(movieDetailDataSourceFactory, config).build()

        return similarMoviePagedList

    }

    fun getMovieDetailNetworkState(): LiveData<NetworkState> {
        return movieDetailDataSource.networkState
    }
}