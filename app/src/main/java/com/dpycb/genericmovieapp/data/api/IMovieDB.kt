package com.dpycb.genericmovieapp.data.api

import com.dpycb.genericmovieapp.data.pojo.MovieDetail
import com.dpycb.genericmovieapp.data.pojo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieDB {

    /*
    all of those are taken from the API part of the site

    Movie details:
    https://api.themoviedb.org/3/movie/200?api_key=3f9255aa7214d5b595af01772145e351
    Popular Movies:
    https://api.themoviedb.org/3/movie/popular?api_key=3f9255aa7214d5b595af01772145e351
    BaseUrl:
    https://api.themoviedb.org/3/
     */

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id")id: Int): Single<MovieDetail>

    @GET("movie/popular")
    fun getPopularMovies(@Query("page")page: Int): Single<MovieResponse>
    //used @Query annotation for the previous one as it is needed in the link

    @GET("movie/top_rated")
    fun getTopMovies(@Query("page")page: Int): Single<MovieResponse>

    @GET("movie/{movie_id}/similar")
    fun getSimilarMovies(@Path("movie_id")movieId: Int, @Query("page")page: Int): Single<MovieResponse>

}