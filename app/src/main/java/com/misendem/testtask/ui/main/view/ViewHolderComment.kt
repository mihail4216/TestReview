package com.misendem.testtask.ui.main.view

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.misendem.testtask.models.Item
import kotlinx.android.synthetic.main.view_review_text.view.*

class ViewHolderComment(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var watcherText: Watcher
    fun onBind(item: Item, textChanged: (String, Int) -> Unit) {
        watcherText = Watcher(textChanged)
        itemView._txtLabelView.text = item.title
        itemView._editTextView.hint = if (adapterPosition == 0) "что вам понравилось" else "что вам не понравилось"
        if (item.valueComment != null) {

            itemView._editTextView.removeTextChangedListener(watcherText)
            itemView._editTextView.setText(item.valueComment)
        }
        itemView._editTextView.addTextChangedListener(watcherText)
    }

    inner class Watcher(var textChanged: (String, Int) -> Unit) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            textChanged(s.toString(), adapterPosition)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}