package com.android.szparag.saymyname.retrofit.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class ModelVersion(@SerializedName("status") @Expose val status: Status? = null,
    @SerializedName("id") @Expose val id: String? = null,
    @SerializedName("created_at") @Expose val createdAt: String? = null)