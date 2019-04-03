package com.misendem.testtask.ui.main

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.misendem.testtask.App
import com.misendem.testtask.models.*
import com.misendem.testtask.ui.main.view.MainView
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

@InjectViewState
class PresenterMainActivity : MvpPresenter<MainView>() {


    private val arrayObservableEditText = arrayListOf<Observable<String>>()
    private val arrayObservableRatingBar = arrayListOf<Observable<Float>>()
    private val subjectPhoto = BehaviorSubject.create<Int>()


    private val disposable = CompositeDisposable()

    val subjectRating = PublishSubject.create<Float>()
    val subjectComment = PublishSubject.create<String>()
    val subjectAll = PublishSubject.create<Any>()


    private val arrayPhoto = arrayListOf<Uri>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadReview()
    }

    fun addPhoto(uri: Uri?) {
        arrayPhoto.add(uri!!)
        subjectAll.onNext(uri)
        subjectPhoto.onNext(arrayPhoto.size)
        viewState.addViewPhoto(uri)
        if (arrayPhoto.size >= 3) {
            viewState.hideBtnAdd()
            viewState.hideGraySquare()
        } else {
            viewState.showBtnAdd()
            viewState.showGraySquare()
        }
    }

    fun onClickGraySquare() {
        viewState.startGallery()
    }

    fun loadReview() {
        viewState.showProgressBar()
        viewState.hideContent()

        disposable.add(App.instance.API.getPreview()
            .flatMap {
                if (it.success)
                    Flowable.just(it.data)
                else
                    Flowable.error(Exception(it.errors?.get(0).toString()))
            }
            .onExceptionResumeNext {
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                viewState.showError()
            }
            .subscribe {
                viewState.hideProgressBar()
                viewState.showContent()
                setModel(it)

            })


    }

    private fun setModel(model: ReviewModel?) {
        if (model != null) {
            setOrder(model.review.order)    //устанавливаем дату
            redirectProperties(model.review.properties)     //устанавливаем вьюхи
            setListenerAllView()        //слушаем эти вьюхи
        }


    }

    private fun setListenerAllView() {
        disposable.add(
            subjectAll.flatMap {
                Observable.combineLatest(arrayObservableEditText, Function<Array<in String>, Boolean> {
                    for (any in it) {
                        if (any.toString().isEmpty())
                            return@Function false
                    }
                    return@Function true
                })
            }
                .flatMap {
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
        arrayPhoto.removeAt(it)
        viewState.deleteBitmap(it)
        viewState.showGraySquare()
        viewState.showBtnAdd()
        subjectAll.onNext(it)
        subjectPhoto.onNext(arrayPhoto.size)

    }

    private fun setOrder(order: Order) {
        var date = ""
        if (order.date_begin.slice(5..6) == order.date_end.slice(5..6)) {
            val beginDay = order.date_begin.slice(8..9)
            val endDay = order.date_end.slice(8..9)
            val month = order.date_begin.slice(5..6)
            val year = order.date_begin.slice(0..3)
            date = "$beginDay - $endDay ${month}. $year"
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

        for (i in items)
            arrayObservableEditText.add(Observable.just(""))
        viewState.addCommentView(items)
    }

    private fun addRatingsOrder(items: ArrayList<Item>) {
        for (i in items)
            arrayObservableRatingBar.add(Observable.just(0f))
        viewState.addRatingsView(items)
//        }
    }


    fun dispose() {
        disposable.clear()
        disposable.dispose()
    }


    fun onRatingChange(rating: Float, pos: Int) {
        arrayObservableRatingBar[pos] = Observable.just(rating)
        subjectRating.onNext(rating)
        subjectAll.onNext(rating)
        viewState.setRating(rating,pos)
    }

    fun onTextChange(text: String, pos: Int) {
        arrayObservableEditText[pos] = Observable.just(text)
        subjectComment.onNext(text)
        subjectAll.onNext(text)
        viewState.setText(text,pos)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

}