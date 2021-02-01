package com.dpycb.genericmovieapp.ui.movie_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dpycb.genericmovieapp.R
import com.dpycb.genericmovieapp.data.api.IMovieDB
import com.dpycb.genericmovieapp.data.api.MovieDBClient
import com.dpycb.genericmovieapp.data.api.POSTER_BASE_URL
import com.dpycb.genericmovieapp.data.pojo.MovieDetail
import com.dpycb.genericmovieapp.data.repo.NetworkState
import kotlinx.android.synthetic.main.activity_single_movie.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class SingleMovie : AppCompatActivity() {
    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieDetailsRepo: MovieDetailsRepo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_movie)

        val movieId: Int = this.intent.getIntExtra("id", 1)

        val apiService: IMovieDB = MovieDBClient.getClient()
        movieDetailsRepo = MovieDetailsRepo(apiService)

        viewModel = getViewModel(movieId)
        initView()

    }

    private fun initView() {
        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        val adapter = MovieDetailsPagedListAdapter(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recycler_similar_list.setHasFixedSize(true)
        recycler_similar_list.layoutManager = layoutManager
        recycler_similar_list.adapter = adapter

        viewModel.similarMovies.observe(this, Observer {
            adapter.submitList(it)
        })
        viewModel.networkState.observe(this, Observer {
            progress_bar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE

            progress_bar.visibility = if (viewModel.isEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (viewModel.isEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!viewModel.isEmpty()) {
                adapter.setNetworkState(it)
            }
        })
    }

    private fun getViewModel(movieId: Int): SingleMovieViewModel {
        return ViewModelProviders.of(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SingleMovieViewModel(
                    movieDetailsRepo,
                    movieId
                ) as T
            }
        })[SingleMovieViewModel::class.java]
    }

    private fun bindUI(it: MovieDetail) {
        //REFRACTOR THIS PART TO BE ABLE TO WATCH
        val title = String.format("<a href='%s'> %s </a>", "https://www.themoviedb.org/movie/" + it.id, it.title)
        movie_title.isClickable = true
        movie_title.movementMethod = LinkMovementMethod.getInstance()
        movie_title.text = Html.fromHtml(title)

        movie_tagline.text = it.tagline

        movie_overview.text = it.overview
        movie_rating.text = it.rating.toString()

        if (!it.releaseDate.isEmpty()) {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd")
            val sdfOutput = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
            movie_release_date.text = sdfOutput.format(sdfInput.parse(it.releaseDate)!!)
        }
        else {
            movie_release_date.text = "-"
        }

        val runtime = String.format("%d минут", it.runtime)
        movie_runtime.text = runtime

        val formatter = DecimalFormat("#,###")
        if (it.budget == 0) {
            movie_budget.text = "-"
        }
        else {
            val budget = String.format("%s долларов", formatter.format(it.budget))
            movie_budget.text = budget
        }
        if (it.revenue == 0L) {
            movie_revenue.text = "-"
        }
        else {
            val revenue = String.format("%s долларов", formatter.format(it.revenue))
            movie_revenue.text = revenue
        }

        val moviePoster = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePoster)
            .into(iv_movie_poster)

    }
}
