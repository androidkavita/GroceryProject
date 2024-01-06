package com.easym.vegie.model.pincode

class GetPinCodeData {

    private var result: List<Result?>? = null
    private var status: String? = null

    fun getResult(): List<Result?>? {
        return result
    }

    fun setResult(result: List<Result?>?) {
        this.result = result
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

}