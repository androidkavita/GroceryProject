package com.easym.vegie.model.applyCouponModel

data class ApplyCouponModel(
    val data: ArrayList<ApplyCouponData>,
    val responseCode: String,
    val responseMessage: String,
    val responseStatus: String
)

data class ApplyCouponData(
    val coupon_code: String,
    val coupon_description: String,
    val created_date: String,
    val end_date: String,
    val id: String,
    val image: String,
    val percentage: String,
    val start_date: String,
    val status: String,
    val updated_date: String
)