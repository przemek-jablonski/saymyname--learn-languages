package com.android.szparag.saymyname.retrofit.models.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class Status(@com.google.gson.annotations.SerializedName("code") @com.google.gson.annotations.Expose val code: Int,
    @com.google.gson.annotations.SerializedName("description") @com.google.gson.annotations.Expose var description: String)