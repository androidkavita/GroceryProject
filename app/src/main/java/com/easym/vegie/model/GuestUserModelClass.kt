package com.easym.vegie.model

data class GuestUserModelClass(
    var `data`: GusetUserData,
    var responseCode: String,
    var responseMessage: String,
    var responseStatus: String
)

data class GusetUserData(
    var created_date: String,
    var device_token: String,
    var id: String,
    var token_no: String,
    var updated_date: String
)