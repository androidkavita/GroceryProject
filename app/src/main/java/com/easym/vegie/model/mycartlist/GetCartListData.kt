package com.easym.vegie.model.mycartlist

class GetCartListData {

    private var cart_list: CartList? = null

    fun getCartList(): CartList? {
        return cart_list
    }

    fun setCartList(cart_list: CartList?) {
        this.cart_list = cart_list
    }


}