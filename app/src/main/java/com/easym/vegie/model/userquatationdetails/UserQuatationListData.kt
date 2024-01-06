package com.easym.vegie.model.userquatationdetails

class UserQuatationListData {

    private var status: String? = null
    private var total : Double?= 0.0
    private var limit_amount : Double?= 0.0
    private var result: List<Result?>? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getResult(): List<Result?>? {
        return result
    }

    fun setResult(result: List<Result?>?) {
        this.result = result
    }

    fun getTotal(): Double? {
        return total
    }

    fun setInt(total : Double?) {
        this.total = total
    }


    fun getLimitAmount(): Double? {
        return limit_amount
    }

    fun setLimitAmount(limitAmount : Double?) {
        this.limit_amount = limitAmount
    }
}