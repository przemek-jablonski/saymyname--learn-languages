package com.android.szparag.saymyname.retrofit.models.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class Output(
    @SerializedName("id") @Expose val id: String?,
    @SerializedName("status") @Expose val status: Status?,
    @SerializedName("created_at") @Expose val createdAt: String?,
    @SerializedName("model") @Expose val model: Model?,
    @SerializedName("input") @Expose val input: Input?,
    @SerializedName("data") @Expose val dataOutput: DataOutput?
) {
  fun getConcepts(): List<Concept>? {
    return dataOutput?.concepts
  }
}