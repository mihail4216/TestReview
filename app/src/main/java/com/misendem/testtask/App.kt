package com.misendem.testtask

import android.app.Application
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.misendem.testtask.network.ApiManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App:Application() {

    private object Holder {
        lateinit var instance: App
    }

    companion object {
        private const val LOG_TAG = "App"
        val instance: App by lazy { Holder.instance }
    }

    init {
        Holder.instance = this
    }

    lateinit var API:ApiManager

    override fun onCreate() {
        super.onCreate()


        val static = Retrofit.Builder()
//            .baseUrl("https://pastebin.com")
            .baseUrl("https://api.myjson.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        API = static.create(ApiManager::class.java)


    }


}