package com.android.szparag.saymyname.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class Status(@SerializedName("code") @Expose val code: Int,
    @SerializedName("description") @Expose var description: String)