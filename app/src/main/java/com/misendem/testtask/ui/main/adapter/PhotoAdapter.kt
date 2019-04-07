package com.misendem.testtask.ui.main.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.misendem.testtask.R
import com.misendem.testtask.ui.main.view.ViewHolderPhoto

class PhotoAdapter: RecyclerView.Adapter<ViewHolderPhoto> (){

    var onClickDelete: ((Int) -> Unit)? = null
    private val arrayBitmap = arrayListOf<Drawable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPhoto {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_photo,parent,false)
        return ViewHolderPhoto(view)
    }

    override fun getItemCount(): Int {
        return arrayBitmap.size
    }

    override fun onBindViewHolder(holder: ViewHolderPhoto, position: Int) {
        holder.onBind(arrayBitmap[position])
        holder.onClickDelete = onClickDelete

    }

    fun addBitmap(bitmap: Drawable){
        arrayBitmap.add(bitmap)
        notifyItemChanged(arrayBitmap.size-1)
    }

    fun deleteBitmap(position:Int){
        arrayBitmap.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,arrayBitmap.size)
    }
}