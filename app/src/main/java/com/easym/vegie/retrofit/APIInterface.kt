package com.easym.vegie.retrofit

import com.easym.vegie.model.UserModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface APIInterface {

    @FormUrlEncoded
    @POST("signup")
    fun callSignUpAPI(
            @Field("username") userName: String?,
            @Field("mobile_number") mobile_number: String?,
            @Field("email") email: String?,
            @Field("password") password: String?,
            @Field("device_token") device_token: String?,
            @Field("device_id") device_id: String?

    ): Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("login")
    fun callLoginAPI(
            @Field("username") userName: String?,
            @Field("password") password: String?,
            @Field("device_token") device_token: String?,
            @Field("device_id") device_id: String?

    ): Observable<CommonDataResponse<UserModel>>
}