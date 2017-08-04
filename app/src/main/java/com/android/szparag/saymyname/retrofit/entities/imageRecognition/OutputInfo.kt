package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class OutputInfo(
    @SerializedName("message") @Expose val message: String? = null,
    @SerializedName("type") @Expose val type: String? = null
)