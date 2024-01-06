package com.easym.vegie.model.minimumamount

data class MinimumAmount(
    val `data`: MinimumAmountData,
    val msg: String,
    val status: String
)

data class MinimumAmountData(
    val created_at: String,
    val id: String,
    val limit_amount: String,
    val status: String
)