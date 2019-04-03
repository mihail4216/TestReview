package com.misendem.testtask.ui.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.misendem.testtask.models.Item

abstract class ItemsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(item:Item)
}