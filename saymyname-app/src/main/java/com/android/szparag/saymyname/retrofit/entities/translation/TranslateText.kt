package com.android.szparag.saymyname.retrofit.entities.translation

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
data class TranslateText(@SerializedName("text") @Expose val text: String)