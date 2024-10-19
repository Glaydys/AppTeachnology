package com.example.myapplication
import android.os.Parcel
import android.os.Parcelable

data class products(
    val id: Int,
    val name_product: String,
    val price: String,
    val image_product: String,
    val category_id: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name_product)
        parcel.writeString(price)
        parcel.writeString(image_product)
        parcel.writeInt(category_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<products> {
        override fun createFromParcel(parcel: Parcel): products {
            return products(parcel)
        }

        override fun newArray(size: Int): Array<products?> {
            return arrayOfNulls(size)
        }
    }
}

