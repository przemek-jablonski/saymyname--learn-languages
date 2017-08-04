package com.android.szparag.saymyname.retrofit.entities.imageRecognition


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class OutputInfo(@com.google.gson.annotations.SerializedName("message") @com.google.gson.annotations.Expose val message: String? = null,
    @com.google.gson.annotations.SerializedName("type") @com.google.gson.annotations.Expose val type: String? = null) {
}