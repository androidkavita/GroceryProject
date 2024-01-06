package com.easym.vegie.retrofit

import com.google.gson.annotations.SerializedName

class CommonResponse<T : Any> {

    @SerializedName("responseCode")
    val responseCode: String? = null

    @SerializedName("responseStatus")
    val responseStatus: String? = null

    @SerializedName("responseMessage")
    val responseMessage: String? = null
}
