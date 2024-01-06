package com.easym.vegie.model.placeorder

class AddOrder {

    private var responseCode: String? = null
    private var responseStatus: String? = null
    private var responseMessage: String? = null
    private var data: AddOrderData? = null

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

    fun getData(): AddOrderData? {
        return data
    }

    fun setData(data: AddOrderData?) {
        this.data = data
    }
}