package com.easym.vegie.model.userquatationdetails

class Product {

    private var view_type: String? = null
    private var category_name: String? = null
    private var serialNo: String? = null
    private var categorySubtotal : Double ?= null

    private var id: String? = null
    private var q_id: String? = null
    private var q_menu_id: String? = null
    private var q_user_id: String? = null
    private var q_total_price: String? = null
    private var q_total_qty: String? = null
    private var unit: String? = null
    private var image: String? = null
    private var menu_name : String? = null
    private var other_name: String? = null
    private var price : String? = null

    private var other_category_name : String ?= null
    private var q_brand_id : String ?= null
    private var q_brand_name : String ?= null
    private var other_q_brand_name : String ?= null

    private var q_brand_price : String?= null
    private var q_brand_unit : String?= null
    private var q_brand_discount :  String?= null
    private var q_brand_tax : String ?= null
    private var is_stock: String? = null
    private var available_qty: String? = null


    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getQId(): String? {
        return q_id
    }

    fun setQId(qId: String?) {
        this.q_id = qId
    }

    fun getQMenuId(): String? {
        return q_menu_id
    }

    fun setQMenuId(qMenuId: String?) {
        this.q_menu_id = qMenuId
    }

    fun getQUserId(): String? {
        return q_user_id
    }

    fun setQUserId(qUserId: String?) {
        this.q_user_id = qUserId
    }

    fun getQTotalPrice(): String? {
        return q_total_price
    }

    fun setQTotalPrice(qTotalPrice: String?) {
        this.q_total_price = qTotalPrice
    }

    fun getQTotalQty(): String? {
        return q_total_qty
    }

    fun setQTotalQty(qTotalQty: String?) {
        this.q_total_qty = qTotalQty
    }

    fun getUnit(): String? {
        return unit
    }

    fun setUnit(unit: String?) {
        this.unit = unit
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getOtherName(): String? {
        return other_name
    }

    fun setOtherName(otherName: String?) {
        this.other_name = otherName
    }

    fun getMenuName(): String? {
        return menu_name
    }

    fun setMenuName(menu_name: String?) {
        this.menu_name = menu_name
    }

    //price
    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun getView_type(): String? {
        return view_type
    }

    fun setView_type(view_type: String?) {
        this.view_type = view_type
    }

    fun getCategory_name(): String? {
        return category_name
    }

    fun setCategory_name(category_name: String?) {
        this.category_name = category_name
    }

    fun getSerialNo(): String? {
        return serialNo
    }

    fun setSerialNo(serialNo: String?) {
        this.serialNo = serialNo
    }

    fun getCategorySubtotal(): Double {
        return categorySubtotal!!
    }

    fun setCategorySubtotal(categorySubtotal: Double) {
        this.categorySubtotal = categorySubtotal
    }

    fun getOther_category_name(): String {
        return other_category_name!!
    }

    fun setOther_category_name(other_category_name: String) {
        this.other_category_name = other_category_name
    }

    fun getQBrandId(): String {
        return q_brand_id!!
    }

    fun setQBrandId(q_brand_id: String) {
        this.q_brand_id = q_brand_id
    }


    fun getQBrandName(): String? {
        return q_brand_name
    }

    fun setQBrandName(q_brand_name: String) {
        this.q_brand_name = q_brand_name
    }

    fun getOtherQBrand_name(): String? {
        return other_q_brand_name
    }

    fun setOtherQBrand_name(other_q_brand_name : String) {
        this.other_q_brand_name  = other_q_brand_name
    }

    fun getQBrandPrice(): String? {
        return q_brand_price
    }

    fun setQBrandPrice(q_brand_price: String?) {
        this.q_brand_price = q_brand_price
    }

    fun getQBrandUnit(): String? {
        return q_brand_unit
    }

    fun setQBrandUnit(q_brand_unit: String?) {
        this.q_brand_unit = q_brand_unit
    }

    fun getQBrandDiscount(): String? {
        return q_brand_discount
    }

    fun setQBrandDiscount(q_brand_discount: String?) {
        this.q_brand_discount = q_brand_discount
    }

    fun getQBrandTax(): String? {
        return q_brand_tax
    }

    fun setQBrandTax(q_brand_tax: String?) {
        this.q_brand_tax = q_brand_tax
    }


    fun getInStock(): String? {
        return is_stock
    }

    fun SetInStock(q_brand_discount: String?) {
        this.is_stock = is_stock
    }

    fun getAvailableQty(): String? {
        return available_qty
    }

    fun setAvailableQty(q_brand_tax: String?) {
        this.available_qty = available_qty
    }
}