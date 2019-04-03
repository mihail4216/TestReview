package com.misendem.testtask.network

import com.google.gson.annotations.SerializedName

class BaseResponse<T> {
    @SerializedName("success")
    val success:Boolean = false
    @SerializedName("platform")
    val platform: String? = null
    @SerializedName("uuid")
    val uuid: String? = null
    @SerializedName("timestamp")
    val timestamp: Int? = null
    @SerializedName("data")
    val data: T? = null
    @SerializedName("errors")
    val errors: ArrayList<Error>? = null
    @SerializedName("actions")
    val actions: ArrayList<Actions>? = null

}

class Error
class Actions