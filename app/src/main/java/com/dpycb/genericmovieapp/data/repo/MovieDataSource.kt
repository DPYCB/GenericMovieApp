package com.dpycb.genericmovieapp.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.dpycb.genericmovieapp.data.api.FIRST_PAGE
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.pojo.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

//extendsion of PageKeyedDataSource is neede because in the link we have pages
class MovieDataSource(private val api: IMovieDB, private val compositeDisposable: CompositeDisposable): PageKeyedDataSource<Int, Movie>() {
    private var page = FIRST_PAGE
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(api.getPopularMovies(page)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.movies, null, page+1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.e("MovieDataSource", it.message!!)
                    }
                ))
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)
        //params.key is taken as the page num
        compositeDisposable.add(api.getPopularMovies(params.key)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.totalPages >= params.key) {
                        callback.onResult(it.movies, params.key+1)
                        networkState.postValue(NetworkState.LOADED)
                    }
                    else {
                        networkState.postValue(NetworkState.END)
                    }
                },
                {
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("MovieDataSource", it.message!!)
                }
            ))
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //Not implementing because it is done in RecyclerView
    }
}