package com.misendem.testtask.ui.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.misendem.testtask.R
import com.misendem.testtask.models.Item
import com.misendem.testtask.ui.main.view.ViewHolderComment

class CommentsAdapter() : RecyclerView.Adapter<ViewHolderComment>() {

    private val items = arrayListOf<Item>()
    lateinit var textChanged: (String, Int) -> Unit
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderComment {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.view_review_text, p0, false)
        return ViewHolderComment(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolderComment, p1: Int) {
        p0.onBind(items[p1],textChanged)
    }

    fun addItem(array: ArrayList<Item>) {
        items.clear()
        items.addAll(array)
//        notifyDataSetChanged()
    }

    fun setText(text: String, pos: Int) {
        items[pos].valueComment = text
//        notifyItemChanged(pos)
    }
}