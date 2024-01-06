package com.easym.vegie.retrofit

import com.google.gson.annotations.SerializedName

class CommonDataResponse <T:Any> {

    @SerializedName("data")
    val data: T? = null

    @SerializedName("responseCode")
    val responseCode: String? = null

    @SerializedName("responseStatus")
    val responseStatus: String? = null

    @SerializedName("responseMessage")
    val responseMessage: String? = null
}