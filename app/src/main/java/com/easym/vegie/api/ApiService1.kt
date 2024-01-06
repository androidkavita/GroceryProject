package com.easym.vegie.api

import com.easym.vegie.model.*
import com.easym.vegie.model.applyCouponModel.ApplyCouponModel
import com.easym.vegie.model.applycoupon.ApplyCoupon
import com.easym.vegie.model.cartcount.UserCartCount
import com.easym.vegie.model.category.GetCategorySubCategory
import com.easym.vegie.model.checkout.Checkout
import com.easym.vegie.model.checksamebrandproduct.CheckSameBrandProduct
import com.easym.vegie.model.coupon.GetCouponList
import com.easym.vegie.model.faq.GetFAQ
import com.easym.vegie.model.getorder.GetOrder
import com.easym.vegie.model.getorderdetail.GetOrderDetail
import com.easym.vegie.model.home.Home
import com.easym.vegie.model.language.GetLanguage
import com.easym.vegie.model.limitday.LimitDay
import com.easym.vegie.model.minimumamount.MinimumAmount
import com.easym.vegie.model.mycart.MyCart
import com.easym.vegie.model.mycartlist.GetCartList
import com.easym.vegie.model.otp.ResendOtp
import com.easym.vegie.model.pincode.GetPincode
import com.easym.vegie.model.placeorder.AddOrder
import com.easym.vegie.model.product.GetProduct
import com.easym.vegie.model.productbrand.GetProductBrand
import com.easym.vegie.model.searchproduct.SearchProduct
import com.easym.vegie.model.shopbyquotation.GetProductQuotation
import com.easym.vegie.model.useraddress.UserAddress
import com.easym.vegie.model.userquatationdetails.UserQuatationList
import com.easym.vegie.model.userquotation.GetUserQuotation
import com.easym.vegie.retrofit.CommonDataResponse
import com.easym.vegie.retrofit.CommonResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable
import javax.inject.Singleton

@Singleton
interface ApiService1 {

    @FormUrlEncoded
    @POST("signup")
    fun callSignUpAPI(
        @Field("username") userName: String?,
        @Field("mobile_number") mobile_number: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("device_token") device_token: String?,
        @Field("device_id") device_id: String
    ): Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("login")
    fun callLoginAPI(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_id") device_id: String
    ): Observable<CommonDataResponse<UserModel>>


    @FormUrlEncoded
    @POST("gustuser")
    fun gustuser(
        @Field("device_token") device_token: String,
    ): Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("verify_otp")
    fun verifyOtp(
        @Field("username") username: String,
        @Field("otp") password: String
    ): Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("resend_otp")
    fun resendOtp(
        @Field("username") username: String
    ): Observable<ResendOtp>

    @FormUrlEncoded
    @POST("get_user")
    fun getUserDetails(
        @Field("user_id") userId: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonDataResponse<UserModel>>

    @Multipart
    @POST("update_profile")
    fun updateUserProfile(
        @Part("user_id") userId: RequestBody,
        @Part("user_name") username: RequestBody,
        @Part("email") userEmail: RequestBody,
        @Part("mobile_number") mobileNo: RequestBody,
        @Part("Authorization") Authorization: RequestBody,
        @Part image: MultipartBody.Part? = null
    )
            : Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("change_password")
    fun changeUserPassword(
        @Field("user_id") userId: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonDataResponse<UserModel>>

    @FormUrlEncoded
    @POST("add_wishlist")
    fun addWishList(
        @Field("user_id") userId: String,
        @Field("menu_id") menuId: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponse<JsonObject>>

    @FormUrlEncoded
    @POST("remove_wishlist")
    fun removeWishList(
        @Field("user_id") userId: String,
        @Field("menu_id") menuId: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponse<JsonObject>>

    @FormUrlEncoded
    @POST("product_list")
    fun homePageApi(
        @Field("user_id") userId: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonDataResponse<DashboardModel>>

    @FormUrlEncoded
    @POST("get_cms")
    fun aboutUs(
        @Field("page_url") pageUrl: String
    ): Observable<CommonDataResponse<JsonObject>>

    @FormUrlEncoded
    @POST("home_api")
    fun getHome(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String
    ): Observable<Home>

    @FormUrlEncoded
    @POST("get_product_category_wise")
    fun getCategorySubCategory(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("lan_code") lan_code: String
    ): Observable<GetCategorySubCategory>

    @FormUrlEncoded
    @POST("get_product_category_wise")
    fun getProduct(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("subcategory_id") category_id: String,
        @Field("lan_code") lan_code: String
    ): Observable<GetProduct>

    @FormUrlEncoded
    @POST("get_coupon_list")
    fun getCouponList(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String
    ): Observable<GetCouponList>

    @POST("get_faq")
    fun get_faq(): Observable<GetFAQ>

    @Multipart
    @POST("addCart")
    fun addCart(
        @Part("user_id") user_id: RequestBody,
        @Part("menu_id") menu_id: RequestBody,
        @Part("qty") qty: RequestBody,
        @Part("verient") verient: RequestBody,
        @Part("brand") brand: RequestBody,
        @Part("Authorization") authorization: RequestBody
    ): Observable<CommonResponseData>


    @FormUrlEncoded
    @POST("removeProductIntoCart")
    fun removeFromCart(
        @Field("user_id") user_id: String,
        @Field("menu_id") menu_id: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("forgot_password")
    fun forgotPassword(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm_password: String
    ): Observable<CommonResponseData>


    /*Till here authentication code is implemented*/
    @FormUrlEncoded
    @POST("get_product_quotation")
    fun getProductQuotation(
        @Field("p_type") p_type: String,
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("lan_code") lan_code: String
    ): Observable<GetProductQuotation>

    @FormUrlEncoded
    @POST("get_cart_list")
    fun getCartList(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("lan_code") lan_code: String
    ): Observable<GetCartList>

    @Multipart
    @POST("search_product")
    fun searchProduct(
        @Part("user_id") user_id: RequestBody,
        @Part("menu_name") menu_name: RequestBody,
        @Part("Authorization") authorization: RequestBody,
        @Part("lan_code") lan_code: RequestBody
    ): Observable<SearchProduct>

    @FormUrlEncoded
    @POST("mycart")
    fun getMyCart(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<MyCart>

    @FormUrlEncoded
    @POST("get_product_brand")
    fun getProductBrand(
        @Field("menu_id") menu_id: String,
        @Field("Authorization") authorization: String,
        @Field("lan_code") lan_code: String
    ): Observable<GetProductBrand>

    @FormUrlEncoded
    @POST("get_language")
    fun getLanguage(
        @Field("Authorization") authorization: String
    ): Observable<GetLanguage>

    @FormUrlEncoded
    @POST("checkout")
    fun checkOut(
        @Field("user_id") user_id: String,
        @Field("coupon_discount") coupon_discount: String,
        @Field("Authorization") authorization: String,
        @Field("checkout_type") checkout_type: String,
        @Field("quotation_id") quotation_id: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<Checkout>

    @FormUrlEncoded
    @POST("check_qty")
    fun checkQuality(
        @Field("user_id") user_id: String,
        @Field("Authorization") authorization: String,
        @Field("address_id") address_id: String,
        @Field("quotation_id") quotation_id: String,
        @Field("checkout_type") checkout_type: String,
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("add_order")
    fun addOrder(
        @Field("user_id") user_id: String,
        @Field("address_id") address_id: String,
        @Field("coupon_code") coupon_code: String,
        @Field("sub_total") sub_total: String,
        @Field("delivery_charge") delivery_charge: String,
        @Field("tax") tax: String,
        @Field("coupon_discount") coupon_discount: String,
        @Field("total") total: String,
        @Field("Authorization") Authorization: String,
        @Field("checkout_type") checkout_type: String,
        @Field("quotation_id") quotation_id: String,
        @Field("transaction_key") transactionKey: String,
//            @Field("advance_payable") advance_payable : String,
//            @Field("remain_payable") remain_payable : String,
        /*   @Field("expected_delivery_datetime") expected_date : String,*/
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("cod") cod: String
    ): Observable<AddOrder>


    @FormUrlEncoded
    @POST("get_order")
    fun getOrder(
        @Field("user_id") user_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<GetOrder>

    @FormUrlEncoded
    @POST("get_order_detail")
    fun getOrderDetail(
        @Field("user_id") user_id: String,
        @Field("order_id") order_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<GetOrderDetail>

    @FormUrlEncoded
    @POST("cancel_order")
    fun cancelOrder(
        @Field("user_id") user_id: String,
        @Field("order_id") order_id: String,
        @Field("reason") reason: String,
        @Field("specify") specify: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("coupon")
    fun coupon(
        @Field("coupon_code") coupon_code: String,
        @Field("amount") amount: String,
        @Field("Authorization") Authorization: String
    ): Observable<ApplyCoupon>

    @FormUrlEncoded
    @POST("get_user_quotation")
    fun getUserQuotation(
        @Field("user_id") coupon_code: String,
        @Field("Authorization") Authorization: String
    ): Observable<GetUserQuotation>

    @FormUrlEncoded
    @POST("add_user_quation")
    fun addUserQuotation(
        @Field("user_id") user_id: String,
        @Field("quotation_name") quotation_name: String,
        @Field("menu_id") menu_id: String,
        @Field("qty") qty: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String,
        @Field("quotation_id") quotation_id: String
    ): Observable<CommonResponseData>


    @FormUrlEncoded
    @POST("update_user_quotation")
    fun updateUserQuotation(
        @Field("user_id") user_id: String,
        @Field("menu_id") menu_id: String,
        @Field("qty") qty: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String,
        @Field("quotation_id") quotation_id: String
    ): Observable<CommonResponseData>


    @FormUrlEncoded
    @POST("get_pincode")
    fun checkPinCode(
        @Field("pincode") pincode: String,
        @Field("Authorization") Authorization: String
    ): Observable<GetPincode>

    @FormUrlEncoded
    @POST("add_address")
    fun addAddress(
        @Field("user_id") user_id: String,
        @Field("address") address: String,
        @Field("locality") locality: String,
        @Field("pincode") pincode: String,
        @Field("state") state: String,
        @Field("city") city: String,
        @Field("Authorization") Authorization: String,
        @Field("lat") lat: String,
        @Field("longi") longi: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("remove_user_quatation")
    fun removeUserQuotation(
        @Field("quatation_id") quatation_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("user_quatation_list")
    fun userQuatationList(
        @Field("quatation_id") quatation_id: String,
        @Field("Authorization") Authorization: String,
        @Field("lan_code") lan_code: String
    ): Observable<UserQuatationList>

    @FormUrlEncoded
    @POST("remove_user_quatation_item")
    fun removeUserQuatationItem(
        @Field("quatation_id") quatation_id: String,
        @Field("menu_id") menu_id: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String
    ): Observable<CommonResponseData>

    //update_quatation_item
    @FormUrlEncoded
    @POST("update_quatation_item")
    fun updateQuatationItem(
        @Field("qty") qty: String,
        @Field("quatation_id") quatation_id: String,
        @Field("menu_id") menu_id: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String
    ): Observable<CommonResponseData>


    @FormUrlEncoded
    @POST("user_cart_count")
    fun userCartCount(
        @Field("user_id") user_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<UserCartCount>

    @FormUrlEncoded
    @POST("update_cart_item")
    fun updateCartItem(
        @Field("qty") qty: String,
        @Field("cart_id") cart_id: String,
        @Field("menu_id") menu_id: String,
        @Field("Authorization") Authorization: String,
        @Field("brand_id") brand_id: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("remove_user_address")
    fun removeUserAddress(
        @Field("address_id") address_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("generate_share_quotation_page")
    fun generateShareQuotationPage(
        @Field("user_id") user_id: String,
        @Field("quotation_id") quotation_id: String,
        @Field("lan_code") lan_code: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("generate_share_cart_page")
    fun generateShareCartPage(
        @Field("user_id") user_id: String,
        @Field("lan_code") lan_code: String,
        @Field("Authorization") Authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("check_by_location")
    fun checkByLocation(
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("Authorization") authorization: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("limit_day")
    fun limitDay(
        @Field("date") date: String,
        @Field("Authorization") authorization: String
    ): Observable<LimitDay>

    @FormUrlEncoded
    @POST("user_address")
    fun getUserAddress(
        @Field("user_id") user_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<UserAddress>

    @FormUrlEncoded
    @POST("return_request")
    fun submitReturnRequest(
        @Field("Authorization") Authorization: String,
        @Field("order_id") order_id: String,
        @Field("return_request") reason: String
    ): Observable<CommonResponseData>

    @FormUrlEncoded
    @POST("check_same_brand_product")
    fun checkSameBrandProduct(
        @Field("Authorization") Authorization: String,
        @Field("user_id") user_id: String,
        @Field("menu_id") menu_id: String,
        @Field("brand_id") brand_id: String
    ): Observable<CheckSameBrandProduct>

    @FormUrlEncoded
    @POST("get_minimum_amount")
    fun getMinimumAmount(
        @Field("Authorization") Authorization: String
    ): Observable<MinimumAmount>

    @FormUrlEncoded
    @POST("send_order_detail")
    suspend fun sendOrderDetail(
        @Field("user_id") user_id: String,
        @Field("order_id") order_id: String,
        @Field("address_id") address_id: String
    ): Observable<MinimumAmount>

    @FormUrlEncoded
    @POST("get_coupon_list")
    fun get_coupon_list(
        @Field("user_id") user_id: String,
        @Field("Authorization") Authorization: String
    ): Observable<ApplyCouponModel>


}