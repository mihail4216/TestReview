package com.misendem.testtask.logic

import com.misendem.testtask.App


class DisplayHelper {
    companion object {

        fun dpToPx(dp: Int): Int {
            val displayMetrics = App.instance.resources.displayMetrics
            return Math.round(dp * displayMetrics.density)
        }

        fun pxToDp(px: Int): Int {
            val displayMetrics = App.instance.resources.displayMetrics
            return Math.round(px / displayMetrics.density)
        }


    }
}