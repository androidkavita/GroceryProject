package com.easym.vegie.model

import com.google.gson.annotations.SerializedName

data class GetGiftResponse(
    @SerializedName("status"  ) var status  : Int?            = null,
    @SerializedName("message" ) var message : String?         = null,
    @SerializedName("data"    ) var data    : ArrayList<GetGiftResponseData> = arrayListOf()
)
data class GetGiftResponseData(
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("image"      ) var image     : String? = null,
    @SerializedName("name"       ) var name      : String? = null,
    @SerializedName("price"      ) var price     : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null
)
