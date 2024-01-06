package com.easym.vegie.model.mycartlist

class CartList {

    private var total: Any? = null
    private var result: List<Result?>? = null
    private var limit_amount : Any?= null

    fun getTotal(): Any? {
        return total
    }

    fun setTotal(total: Any?) {
        this.total = total
    }

    fun getResult(): List<Result?>? {
        return result
    }

    fun setResult(result: List<Result?>?) {
        this.result = result
    }

    fun getLimitAmount(): Any? {
        return limit_amount
    }

    fun setLimitAmount(total: Any?) {
        this.limit_amount = total
    }

}