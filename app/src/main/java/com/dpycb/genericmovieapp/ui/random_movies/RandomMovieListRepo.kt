package com.dpycb.genericmovieapp.ui.random_movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.api.POST_PER_PAGE
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.repo.NetworkState
import com.dpycb.genericmovieapp.data.repo.RandomMovieDataSource
import com.dpycb.genericmovieapp.data.repo.RandomMovieDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RandomMovieListRepo(private val api: IMovieDB) {
    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var movieDataSourceFactory: RandomMovieDataSourceFactory

    fun fetchMoviePagedList(compositeDisposable: CompositeDisposable): LiveData<PagedList<Movie>> {
        movieDataSourceFactory = RandomMovieDataSourceFactory(api, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()


        moviePagedList = LivePagedListBuilder(movieDataSourceFactory, config).build()
        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<RandomMovieDataSource, NetworkState>(
            movieDataSourceFactory.moviesLiveData, RandomMovieDataSource::networkState)
    }

}