package com.android.szparag.saymyname.retrofit.entities.imageRecognition


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class ModelVersion(@com.google.gson.annotations.SerializedName("status") @com.google.gson.annotations.Expose val status: Status? = null,
    @com.google.gson.annotations.SerializedName("id") @com.google.gson.annotations.Expose val id: String? = null,
    @com.google.gson.annotations.SerializedName("created_at") @com.google.gson.annotations.Expose val createdAt: String? = null)