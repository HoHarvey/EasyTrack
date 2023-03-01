package com.tri_devs.easytrack.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var name: String?,
    var description: String?,
    var quantity: Int? = 0,
    var upcNumber: Long? = 0L,
    var retailPrice: String?,
    var salesPrice: String? = "",
    var startSalesDate: String? = "",
    var endSalesDate: String? = ""
) : Parcelable {}
