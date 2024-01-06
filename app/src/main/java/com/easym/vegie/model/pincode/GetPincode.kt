package com.easym.vegie.model.pincode

class GetPincode {

    private var responseCode: String? = null
    private var responseStatus: String? = null
    private var responseMessage: String? = null
    private var data: GetPinCodeData? = null

    fun getResponseCode(): String? {
        return responseCode
    }

    fun setResponseCode(responseCode: String?) {
        this.responseCode = responseCode
    }

    fun getResponseStatus(): String? {
        return responseStatus
    }

    fun setResponseStatus(responseStatus: String?) {
        this.responseStatus = responseStatus
    }

    fun getResponseMessage(): String? {
        return responseMessage
    }

    fun setResponseMessage(responseMessage: String?) {
        this.responseMessage = responseMessage
    }

    fun getData(): GetPinCodeData? {
        return data
    }

    fun setData(data: GetPinCodeData?) {
        this.data = data
    }

}