package com.dpycb.genericmovieapp.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.api.POST_PER_PAGE
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.repo.MovieDataSource
import com.dpycb.genericmovieapp.data.repo.MovieDataSourceFactory
import com.dpycb.genericmovieapp.data.repo.NetworkState
import io.reactivex.disposables.CompositeDisposable

class MoviePopularListRepo(private val api: IMovieDB) {
    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var movieDataSourceFactory: MovieDataSourceFactory

    fun fetchMoviePagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = MovieDataSourceFactory(api, compositeDisposable)

        //Config file for the creating livePagedList
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()


        moviePagedList = LivePagedListBuilder(movieDataSourceFactory, config).build()
        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        //getting mutable networkState from movieDatasourceFactory and transforming it into LiveData
        return Transformations.switchMap<MovieDataSource, NetworkState>(
            movieDataSourceFactory.moviesLiveData, MovieDataSource::networkState)
    }

}