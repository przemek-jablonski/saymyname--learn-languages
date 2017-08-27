package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
data class Concept(
    @SerializedName("id") @Expose val id: String,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("app_id") @Expose val app_id: String?,
    @SerializedName("value") @Expose val value: Float
)