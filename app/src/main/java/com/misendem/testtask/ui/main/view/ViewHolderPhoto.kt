package com.misendem.testtask.ui.main.view

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import com.misendem.testtask.logic.DisplayHelper
import kotlinx.android.synthetic.main.view_holder_photo.view.*

class ViewHolderPhoto(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var onClickDelete: ((Int) -> Unit)? = null

    fun onBind(bitmap: Bitmap) {
        resizeImage(bitmap)
        itemView._imagePhoto.setImageBitmap(bitmap)
        itemView._btnDeletePhoto.setOnClickListener {
            onClickDelete?.invoke(adapterPosition)
        }
    }

    fun resizeImage(bitmap: Bitmap) {
        val height = DisplayHelper.dpToPx(120)
        itemView._imagePhoto.layoutParams.height = height
        itemView._imagePhoto.layoutParams.width = (height.toFloat() / bitmap.height * bitmap.width).toInt()
    }
}