package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import android.util.Base64
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
class Image() {

  @SerializedName("base64") @Expose lateinit var imageBase64: String

  constructor(imageBase64: String) : this() {
    this.imageBase64 = imageBase64
  }

  constructor(imageByteArray: ByteArray) : this() {
    this.imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
  }
}