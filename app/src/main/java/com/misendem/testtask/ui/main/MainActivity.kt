package com.misendem.testtask.ui.main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.misendem.testtask.R
import com.misendem.testtask.models.Item
import com.misendem.testtask.ui.main.adapter.CommentsAdapter
import com.misendem.testtask.ui.main.adapter.PhotoAdapter
import com.misendem.testtask.ui.main.adapter.RatingsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.*
import java.io.IOException


class MainActivity : MvpAppCompatActivity(), MainView {

    companion object {
        private const val REQUEST_GALLERY = 101
    }

    @InjectPresenter
    lateinit var mPresenter: PresenterMainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
    }


    private fun initUi() {

        initPhotoBlock()
        _tryAgainLoad.setOnClickListener {
            mPresenter.loadReview()
        }
        _reviewRatingList.adapter = RatingsAdapter().apply {
            rating = { rating, position ->
                mPresenter.onRatingChange(rating, position)
            }
        }
        _reviewTextList.adapter = CommentsAdapter().apply {
            textChanged = { text, position ->
                mPresenter.onTextChange(text, position)

            }
        }
        _reviewTextList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        _reviewRatingList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun hideError() {
        _errorView.visibility = View.GONE
    }

    private fun initPhotoBlock() {

        PhotoAdapter().also { adapter ->
            adapter.onClickDelete = {
                mPresenter.deletePhoto(it)
            }

            _recyclerViewPhoto.adapter = adapter
            _recyclerViewPhoto.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        _addPhoto.setOnClickListener {
            mPresenter.onClickAddPhoto()
        }
        _fakePhoto.setOnClickListener {
            mPresenter.onClickGraySquare()
        }

        _btnExit.setOnClickListener { 
            startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))}
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val imagePath = data?.data
                    try {
                        mPresenter.addPhoto(imagePath)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    override fun hideGraySquare() {
        _fakePhoto.visibility = View.GONE
    }

    override fun showGraySquare() {
        _fakePhoto.visibility = View.VISIBLE

    }

    override fun showProgressBar() {
        _progressBar.visibility = View.VISIBLE
    }

    override fun hideContent() {
        _contentContainer.visibility = View.GONE

    }

    override fun hideProgressBar() {
        _progressBar.visibility = View.GONE

    }

    override fun showContent() {
        _contentContainer.visibility = View.VISIBLE

    }

    override fun addViewPhoto(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        if (bitmap != null) {
            val roundBitmap = RoundedBitmapDrawableFactory.create(resources, bitmap)
            val roundPx = bitmap.width * 0.025f
            roundBitmap.cornerRadius = roundPx
            (_recyclerViewPhoto.adapter as PhotoAdapter).addBitmap(roundBitmap)

        }
    }

    override fun deleteBitmap(it: Int) {
        (_recyclerViewPhoto.adapter as PhotoAdapter).deleteBitmap(it)
    }

    override fun startGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY)
    }

    override fun setAverageRating(rating: Float?) {
        val rating = String.format("%.2g", rating)
        _txtAverageRating.text = rating
        _contAverageRating.setBackgroundColor(resources.getColor(R.color.colorAccent))
    }

    override fun setOrderID(id: String) {
        _idOrder.text = id
    }

    override fun setOrderDate(date: String) {
        _dateOrder.text = date
    }

    override fun showError() {
        _errorView.visibility = View.VISIBLE
        _errorView.setOnClickListener {
            _errorView.visibility = View.GONE
        }
    }

    override fun setTextBtnAdd(text: String) {
        _addPhoto.text = text
    }

    override fun hideBtnAdd() {
        _addPhoto.visibility = View.GONE
    }

    override fun showBtnAdd() {
        _addPhoto.visibility = View.VISIBLE

    }

    override fun addCommentView(items: ArrayList<Item>) {
        (_reviewTextList.adapter as CommentsAdapter).addItem(items)
    }

    override fun setText(text: String, pos: Int) {
        (_reviewTextList.adapter as CommentsAdapter).setText(text, pos)
    }

    override fun setRating(rating: Float, pos: Int) {
        (_reviewRatingList.adapter as RatingsAdapter).setRating(rating, pos)

    }

    override fun addRatingsView(items: ArrayList<Item>) {
        (_reviewRatingList.adapter as RatingsAdapter).setItems(items)

    }

    override fun setFailedBtnSend() {

        _btnSendFeedback.setBackgroundColor(Color.parseColor("#F6F6F6"))
        _btnSendFeedback.setTextColor(Color.parseColor("#999999"))
        _btnSendFeedback.isClickable = false

    }

    override fun setSuccessBtnSend() {
        _btnSendFeedback.background = resources.getDrawable(R.drawable.background_send_feedback)
        _btnSendFeedback.setTextColor(Color.WHITE)
        _btnSendFeedback.isClickable = true
    }

    override fun onBackPressed() {
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
    }

}
