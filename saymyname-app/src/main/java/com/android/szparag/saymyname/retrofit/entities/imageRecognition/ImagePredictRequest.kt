package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.*

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
class ImagePredictRequest() {

  @SerializedName("inputs") @Expose lateinit var inputs: List<Input>

  constructor(inputs: List<Input>) : this() {
    this.inputs = inputs
  }

  constructor(imageByteArray: ByteArray) :this() {
    inputs = listOf(Input(DataInput(Image(imageByteArray))))
  }

  constructor(imageBase64: String): this() {
    inputs = listOf(Input(DataInput(Image(imageBase64))))
  }

}