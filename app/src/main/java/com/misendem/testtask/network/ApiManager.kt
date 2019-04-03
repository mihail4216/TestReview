package com.misendem.testtask.network

import com.misendem.testtask.models.ReviewModel
import io.reactivex.Flowable
import retrofit2.http.GET

interface ApiManager {

//    @GET("/6TGRH49y")
    @GET("/bins/16oh5y")
//    @GET("/bins/b6uza")
    fun getPreview(): Flowable<BaseResponse<ReviewModel>>

}