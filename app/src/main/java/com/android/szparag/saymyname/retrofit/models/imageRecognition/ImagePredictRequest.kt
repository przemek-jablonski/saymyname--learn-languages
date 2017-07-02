package com.android.szparag.saymyname.retrofit.models.imageRecognition

import com.google.gson.annotations.*
/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class ImagePredictRequest(@SerializedName("inputs") @Expose val inputs : List<Input>)