package com.misendem.testtask.ui.main

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.misendem.testtask.App
import com.misendem.testtask.models.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.text.DateFormatSymbols

@InjectViewState
class PresenterMainActivity : MvpPresenter<MainView>() {

    companion object {
        const val NAME_FILE_PREFERENCES = "shared"
        const val KEY_MODEL = "modelKey"
    }

    private val arrayObservableEditText = arrayListOf<Observable<String>>()
    private val arrayObservableRatingBar = arrayListOf<Observable<Float>>()
    private val subjectPhoto = BehaviorSubject.create<Int>()


    private val disposable = CompositeDisposable()

    private val subjectRating = PublishSubject.create<Float>()
    private val subjectComment = PublishSubject.create<String>()
    private val subjectAll = PublishSubject.create<Any>()

    private var mModelView = SavingModel(
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    )

    private val sharedPreferences = App.instance.getSharedPreferences(NAME_FILE_PREFERENCES, MODE_PRIVATE)
    private val arrayPhoto = arrayListOf<Uri>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadReview()
    }


    override fun onDestroy() {
        Log.e("OnDestroy", "good")
        dispose()
        super.onDestroy()
    }

    fun addPhoto(uri: Uri?) {
        mModelView.arraySavingListPhoto.add(uri!!.toString())
        arrayPhoto.add(uri)
        subjectAll.onNext(uri)
        subjectPhoto.onNext(arrayPhoto.size)
        viewState.addViewPhoto(uri)
        checkArrayPhoto()
    }


    fun onClickGraySquare() {
        viewState.startGallery()
    }

    fun loadReview() {
        viewState.showProgressBar()
        viewState.hideContent()
        viewState.hideError()

        disposable.add(App.instance.API.getPreview()
            .flatMap {
                if (it.success)
                    Flowable.just(it.data)
                else
                    Flowable.error(Exception(it.errors?.get(0).toString()))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                it.printStackTrace()
                viewState.showError()
                viewState.hideProgressBar()
            }
            .subscribe({
                viewState.hideProgressBar()
                viewState.showContent()
                setModel(it)

            }, {
                it.printStackTrace()
            })
        )


    }

    private fun setModel(model: Data?) {
        if (model != null) {
            setOrder(model.review.order)    //устанавливаем дату
            redirectProperties(model.review.properties)     //устанавливаем вьюхи
            setListenerAllView()        //слушаем эти вьюхи
            readSaveData()          // чтение сохраненых данных
            restoreData()       //  востановление данных
        }


    }

    private fun restoreData() {
        restoreRatingsFilds()
        restoreTextFields()
        restorePhotoFields()
        subjectAll.onNext(true)
    }

    private fun restorePhotoFields() {
        for (i in mModelView.arraySavingListPhoto) {
            val uri = Uri.parse(i)
            arrayPhoto.add(uri)
            viewState.addViewPhoto(uri)
        }
        subjectPhoto.onNext(mModelView.arraySavingListPhoto.size)
        checkArrayPhoto()

    }

    private fun restoreTextFields() {
        for (i in 0 until mModelView.arraySavingListComment.size) {
            viewState.setText(mModelView.arraySavingListComment[i], i)
            arrayObservableEditText[i] = Observable.just(mModelView.arraySavingListComment[i])
        }
    }

    private fun restoreRatingsFilds() {
        for (i in 0 until mModelView.arraySavingListRating.size) {
            viewState.setRating(mModelView.arraySavingListRating[i], i)
            arrayObservableRatingBar[i] = Observable.just(mModelView.arraySavingListRating[i])
            subjectRating.onNext(mModelView.arraySavingListRating[i])
        }
    }

    private fun checkArrayPhoto() {
        when {
            arrayPhoto.size == 0 -> {
                viewState.showBtnAdd()
                viewState.showGraySquare()
                viewState.setTextBtnAdd("Добавить")
            }
            arrayPhoto.size in 1..2 -> {
                viewState.hideGraySquare()
                viewState.showBtnAdd()
                viewState.setTextBtnAdd("Добавить еще")
            }
            else -> {
                viewState.hideBtnAdd()
                viewState.hideGraySquare()
            }
        }
    }

    private fun setListenerAllView() {
        disposable.add(
            subjectAll
                .flatMap {
                    //проверка на пустоту строк
                    Observable.combineLatest(arrayObservableEditText, Function<Array<in String>, Boolean> {
                        for (any in it) {
                            if (any.toString().isEmpty())
                                return@Function false
                        }
                        return@Function true
                    })
                }
                .flatMap {
                    // проверка на пустоту рейтинга
                    if (it)
                        Observable.combineLatest(arrayObservableRatingBar, Function<Array<in Float>, Boolean> {
                            //                вычисляем среднее значение всех рейтинг вью
                            for (any in it) {
                                val d = any as Float
                                if (d == 0f) return@Function false
                            }
                            return@Function true
                        })
                    else
                        Observable.just(false)
                }
                .flatMap {
                    //..проверка на заполненость картинок
                    if (it)
                        subjectPhoto.flatMap {
                            if (it == 3)
                                Observable.just(true)
                            else
                                Observable.just(false)
                        }
                    else
                        Observable.just(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    saveData()
                    if (it)
                        viewState.setSuccessBtnSend()
                    else
                        viewState.setFailedBtnSend()
                }
        )

        disposable.add(
            subjectRating.flatMap {
                Observable.combineLatest(arrayObservableRatingBar, Function<Array<in Float>, Float> {
                    //                вычисляем среднее значение всех рейтинг вью
                    averageArray(it)
                    return@Function 0F
                })
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }

        )


    }

    private fun averageArray(array: Array<in Float>): Float {
        var sum = 0F
        for (i in array) {
            sum += i as Float
        }
        viewState.setAverageRating(sum / array.size)
        return sum / array.size
    }

    fun onClickAddPhoto() {
        viewState.startGallery()
    }

    fun deletePhoto(it: Int) {
        mModelView.arraySavingListPhoto.removeAt(it)
        arrayPhoto.removeAt(it)
        viewState.deleteBitmap(it)
        viewState.showGraySquare()
        viewState.showBtnAdd()
        subjectAll.onNext(it)
        subjectPhoto.onNext(arrayPhoto.size)
        checkArrayPhoto()

    }

    private fun setOrder(order: Order) {
        var date = ""
        if (order.date_begin.slice(5..6) == order.date_end.slice(5..6)) {
            val beginDay = order.date_begin.slice(8..9)
            val endDay = order.date_end.slice(8..9)
            val month = order.date_begin.slice(5..6)
            val year = order.date_begin.slice(0..3)
            val shortMonth = DateFormatSymbols().shortMonths[month.toInt() - 1]
            date = "$beginDay - $endDay $shortMonth $year"
        } else {
            // меняем формат этой строки
        }
        viewState.setOrderDate(date)
        viewState.setOrderID("бронь № ${order.id}")
    }

    private fun redirectProperties(arrayProperties: ArrayList<Properties>?) {
        if (arrayProperties != null)
            for (i in arrayProperties) {
                // взависимости от типа расперделяем и добавляем вьюху
                when (i.group_name) {
                    GroupName.review_ratings_order.name -> addRatingsOrder(i.items)
                    GroupName.review_text_order.name -> addTextOrder(i.items)
                }
            }
    }

    private fun addTextOrder(items: ArrayList<Item>) {

        for (i in items) {
            arrayObservableEditText.add(Observable.just(""))
            mModelView.arraySavingListComment.add("")
        }
        viewState.addCommentView(items)
    }

    private fun addRatingsOrder(items: ArrayList<Item>) {
        for (i in items) {
            arrayObservableRatingBar.add(Observable.just(0f))
            mModelView.arraySavingListRating.add(0f)
        }
        viewState.addRatingsView(items)
    }


    private fun dispose() {
        disposable.clear()
        disposable.dispose()
    }


    fun onRatingChange(rating: Float, pos: Int) {
        mModelView.arraySavingListRating[pos] = rating
        arrayObservableRatingBar[pos] = Observable.just(rating)
        subjectRating.onNext(rating)
        subjectAll.onNext(rating)
        viewState.setRating(rating, pos)
    }

    fun onTextChange(text: String, pos: Int) {
        mModelView.arraySavingListComment[pos] = text
        arrayObservableEditText[pos] = Observable.just(text)
        subjectComment.onNext(text)
        subjectAll.onNext(text)
        viewState.setText(text, pos)
    }

    private fun readSaveData() {
        val gson = GsonBuilder().create()
        val model = gson.fromJson<SavingModel>(sharedPreferences.getString(KEY_MODEL, ""), SavingModel::class.java)
        mModelView = model
    }

    private fun saveData() {
        val model = mModelView
        val modelJson = Gson().toJson(model)
        val edit = sharedPreferences.edit()
        Log.e("save", modelJson)
        edit.putString(KEY_MODEL, modelJson)
        edit.apply()
    }

    private fun clearData() {
        mModelView.arraySavingListComment.clear()
        mModelView.arraySavingListPhoto.clear()
        mModelView.arraySavingListRating.clear()
        saveData()
        Log.e("delete", "Pizdec")
    }

    fun onClickExit() {
        clearData()
        viewState.finishApp()
    }

    fun onBackPressed() {
        clearData()
    }

}