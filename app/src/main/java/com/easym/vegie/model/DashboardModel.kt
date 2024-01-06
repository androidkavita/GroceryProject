package com.easym.vegie.model

import android.os.Parcel
import android.os.Parcelable

data class DashboardModel(val recommended_menu_list: List<RecommendedData>,
                          val seasonal_product: List<SeasonalData>,
                          val discounted_menu_list: List<DiscountedData>) {


    data class RecommendedData(
            val menu_name: String,
            val image: String,
            val price: String,
            val description: String,
            val quantity: String,
            val unit: String
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(menu_name)
            writeString(image)
            writeString(price)
            writeString(description)
            writeString(quantity)
            writeString(unit)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<RecommendedData> = object : Parcelable.Creator<RecommendedData> {
                override fun createFromParcel(source: Parcel): RecommendedData = RecommendedData(source)
                override fun newArray(size: Int): Array<RecommendedData?> = arrayOfNulls(size)
            }
        }
    }

    data class SeasonalData(
            val menu_name: String,
            val image: String,
            val price: String,
            val description: String,
            val quantity: String,
            val unit: String
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(menu_name)
            writeString(image)
            writeString(price)
            writeString(description)
            writeString(quantity)
            writeString(unit)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SeasonalData> = object : Parcelable.Creator<SeasonalData> {
                override fun createFromParcel(source: Parcel): SeasonalData = SeasonalData(source)
                override fun newArray(size: Int): Array<SeasonalData?> = arrayOfNulls(size)
            }
        }
    }

    data class DiscountedData(
            val menu_name: String,
            val image: String,
            val price: String,
            val description: String,
            val quantity: String,
            val unit: String,
            val discount: String
    ) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!,
                source.readString()!!
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(menu_name)
            writeString(image)
            writeString(price)
            writeString(description)
            writeString(quantity)
            writeString(unit)
            writeString(discount)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<DiscountedData> = object : Parcelable.Creator<DiscountedData> {
                override fun createFromParcel(source: Parcel): DiscountedData = DiscountedData(source)
                override fun newArray(size: Int): Array<DiscountedData?> = arrayOfNulls(size)
            }
        }
    }
}