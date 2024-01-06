package com.easym.vegie.model.placeorder

class AddOrderData {

    private var order_id: Int? = null

    fun getOrderId(): Int? {
        return order_id
    }

    fun setOrderId(orderId: Int?) {
        this.order_id = orderId
    }
}