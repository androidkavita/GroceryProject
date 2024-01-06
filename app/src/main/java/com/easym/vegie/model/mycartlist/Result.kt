package com.easym.vegie.model.mycartlist

class Result {

    private var name: String? = null
    private var product: List<Product?>? = null
    private var subtotal : Any?= null
    private var other_category_name : String?= null

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getProduct(): List<Product?>? {
        return product
    }

    fun setProduct(product: List<Product?>?) {
        this.product = product
    }

    fun getSubtotal() : Any? {
        return subtotal
    }

    fun setSubtotal(subtotal: Any?) {
        this.subtotal = subtotal
    }


    fun getOtherCategoryName() : String? {
        return other_category_name
    }

    fun setOtherCategoryName(other_category_name: String?) {
        this.other_category_name = other_category_name
    }
}