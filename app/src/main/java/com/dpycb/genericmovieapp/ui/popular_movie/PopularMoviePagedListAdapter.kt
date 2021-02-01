package com.dpycb.genericmovieapp.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dpycb.genericmovieapp.R
import com.dpycb.genericmovieapp.data.api.POSTER_BASE_URL
import com.dpycb.genericmovieapp.data.pojo.Movie
import com.dpycb.genericmovieapp.data.repo.NetworkState
import com.dpycb.genericmovieapp.ui.movie_details.SingleMovie
import kotlinx.android.synthetic.main.activity_popular_movies.view.*
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*


//WTF IS GOING ON IN THE ITEMCOUNTS AND ROWS????
class PopularMoviePagedListAdapter(val context: Context): PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffUtil()) {

    //to determine which to shiow in recycler view
    val MOVIE_TYPE = 1
    val NETWORK_TYPE = 2

    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if (viewType == MOVIE_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieViewHolder(view)
        }
        else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_TYPE) {
            (holder as MovieViewHolder).bind(getItem(position), context)
        }
        else {
            (holder as NetworkStateViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState !=null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + (if (hasExtraRow()) 1 else 0)
    }

    override fun getItemViewType(position: Int): Int {
        if (hasExtraRow() && position == itemCount - 1) {
            return NETWORK_TYPE
        }
        else {
            return MOVIE_TYPE
        }
    }

    fun setNetworkState(newState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            }
            else {
                notifyItemInserted(super.getItemCount())
            }
        }
        else if (hasExtraRow && previousState != newState) {
            notifyItemChanged(itemCount - 1)
        }

    }

    class MovieDiffUtil: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    class MovieViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie?, context: Context) {
            itemView.cv_movie_title.text = movie?.title
            itemView.cv_movie_release_date.text = movie?.releaseDate

            val posterUrl = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(posterUrl)
                .into(itemView.cv_iv_movie_poster)
            itemView.setOnClickListener {
                val intent = Intent(context, SingleMovie::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progress_bar_item.visibility = View.VISIBLE
            }
            else {
                itemView.progress_bar_item.visibility = View.GONE
            }

            if (networkState != null && (networkState == NetworkState.ERROR || networkState == NetworkState.END)) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.message
            }
            else {
                itemView.error_msg_item.visibility = View.GONE
            }
        }
    }
}