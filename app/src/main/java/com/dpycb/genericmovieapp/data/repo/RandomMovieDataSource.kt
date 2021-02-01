package com.dpycb.genericmovieapp.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.pojo.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RandomMovieDataSource(private val api: IMovieDB, private val compositeDisposable: CompositeDisposable): PageKeyedDataSource<Int, Movie>() {
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        networkState.postValue(NetworkState.LOADING)

        val randomPage = (1..20).random()

        compositeDisposable.add(api.getTopMovies(randomPage)
            .subscribeOn(Schedulers.io())
            .subscribe({
                callback.onResult(it.movies, null, randomPage + 1)
                networkState.postValue(NetworkState.LOADED)
            },
                {
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("RandomMovieDatSource", it.message!!)
                }))
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //not needed
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        //Not implementing because it is done in RecyclerView
    }
}