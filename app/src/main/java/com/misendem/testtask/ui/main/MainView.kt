package com.misendem.testtask.ui.main

import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.*
import com.misendem.testtask.models.Item

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {

    @StateStrategyType(SingleStateStrategy::class)
    fun showError()

    fun setOrderID(id: String)
    fun setOrderDate(date: String)
    fun setFailedBtnSend()
    fun setSuccessBtnSend()
    fun setAverageRating(rating: Float?)

    @StateStrategyType(SkipStrategy::class)
    fun startGallery()

    @StateStrategyType(AddToEndStrategy::class)
    fun deleteBitmap(it: Int)

    @StateStrategyType(AddToEndStrategy::class)
    fun addViewPhoto(uri: Uri)

    fun hideBtnAdd()
    fun showBtnAdd()
    fun showProgressBar()
    fun hideContent()
    fun hideProgressBar()
    fun showContent()
    fun hideGraySquare()
    fun showGraySquare()

//    @StateStrategyType(SkipStrategy::class)
    fun addCommentView(items: ArrayList<Item>)

//    @StateStrategyType(SkipStrategy::class)
    fun addRatingsView(items: ArrayList<Item>)

    fun setText(text: String, pos: Int)
    fun setRating(rating: Float, pos: Int)
}