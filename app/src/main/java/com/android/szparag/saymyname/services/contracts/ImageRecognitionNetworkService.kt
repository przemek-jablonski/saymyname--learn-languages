package com.android.szparag.saymyname.services.contracts

import com.android.szparag.saymyname.retrofit.apis.ApiImageRecognitionClarifai
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface ImageRecognitionNetworkService : NetworkService {

  interface ImageRecognitionNetworkResult {
    fun onSucceeded(concepts: List<Concept>)
    fun onFailed()
  }

  fun requestImageProcessing(
      modelId: String,
      imageByteArray: ByteArray,
      callback: ImageRecognitionNetworkResult
  )

}