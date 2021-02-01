package com.dpycb.genericmovieapp.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.dpycb.genericmovieapp.data.api.FIRST_PAGE
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.pojo.MovieDetail
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDetailDataSource(private val api: IMovieDB, private val compositeDisposable: CompositeDisposable, private val movieId: Int):
    PageKeyedDataSource<Int, Movie>(){
    private var page = FIRST_PAGE

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _movieDetailResponse = MutableLiveData<MovieDetail>()
    val movieDetailResponse: LiveData<MovieDetail>
        get() = _movieDetailResponse

    fun fetchMovieDetails() {
        _networkState.postValue(NetworkState.LOADING)

            compositeDisposable.add(
                api.getMovieDetails(movieId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _movieDetailResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.e("MovieDetailResponse", it.message!!)
                        }
                    )
            )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {

        _networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(api.getSimilarMovies(movieId, page)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    callback.onResult(it.movies, null, page+1)
                    _networkState.postValue(NetworkState.LOADED)
                },
                {
                    _networkState.postValue(NetworkState.ERROR)
                    Log.e("MovieDataSource", it.message!!)
                }
            ))
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        _networkState.postValue(NetworkState.LOADING)
        //params.key is taken as the page num
        compositeDisposable.add(api.getSimilarMovies(movieId, params.key)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.totalPages >= params.key) {
                        callback.onResult(it.movies, params.key+1)
                        _networkState.postValue(NetworkState.LOADED)
                    }
                    else {
                        _networkState.postValue(NetworkState.END)
                    }
                },
                {
                    _networkState.postValue(NetworkState.ERROR)
                    Log.e("MovieDataSource", it.message!!)
                }
            ))
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
    }


}