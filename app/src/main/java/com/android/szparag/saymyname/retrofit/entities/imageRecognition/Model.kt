package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class Model(
    @SerializedName("name") @Expose val name: String? = null,
    @SerializedName("id") @Expose val id: String? = null,
    @SerializedName("created_at") @Expose val createdAt: String? = null,
    @SerializedName("app_id") @Expose val appId: Any? = null,
    @SerializedName("output_info") @Expose val outputInfo: OutputInfo? = null,
    @SerializedName("model_version") @Expose val modelVersion: ModelVersion? = null
)