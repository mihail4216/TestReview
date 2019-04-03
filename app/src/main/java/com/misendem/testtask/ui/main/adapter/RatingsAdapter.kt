package com.misendem.testtask.ui.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.misendem.testtask.R
import com.misendem.testtask.models.Item
import com.misendem.testtask.ui.main.view.ViewHolderRatings

class RatingsAdapter() : RecyclerView.Adapter<ViewHolderRatings>() {
    private val items = arrayListOf<Item>()
    lateinit var rating: (Float, Int) -> Unit
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderRatings {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.view_review_ratings, p0, false)
        return ViewHolderRatings(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolderRatings, p1: Int) {
        p0.onBind(items[p1], rating)
    }

    fun setItems(array: ArrayList<Item>) {
        items.clear()
        items.addAll(array)
    }

    fun setRating(rating: Float, pos: Int) {
        items[pos].valueRating = rating
    }
}