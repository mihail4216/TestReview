package com.misendem.testtask.ui.main.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.misendem.testtask.models.Item
import kotlinx.android.synthetic.main.view_review_ratings.view.*

class ViewHolderRatings(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(item: Item, rating: (Float, Int) -> Unit) {
        itemView._txtRatingsView.text = item.title
        if (item.valueRating != null){
            itemView._ratingBarView.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->  }
            itemView._ratingBarView.rating = item.valueRating!!
        }
        itemView._ratingBarView.setOnRatingBarChangeListener { bar, rating, fromUSer ->
            rating(rating, adapterPosition)
        }
    }


}