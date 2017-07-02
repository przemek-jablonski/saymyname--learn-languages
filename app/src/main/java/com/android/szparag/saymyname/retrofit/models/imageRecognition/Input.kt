package com.android.szparag.saymyname.retrofit.models.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
data class Input(@SerializedName("data") @Expose val dataInput: DataInput)