package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
data class DataOutput(
    @SerializedName("concepts") @Expose val concepts: List<Concept>
)