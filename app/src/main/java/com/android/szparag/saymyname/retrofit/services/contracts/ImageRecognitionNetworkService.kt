package com.android.szparag.saymyname.retrofit.services.contracts

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.utils.logMethod


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface ImageRecognitionNetworkService : NetworkService {

  interface ImageRecognitionNetworkResult {
    @CallSuper fun onSucceeded(concepts: List<Concept>) {
      logMethod(optionalString = concepts.toString())
    }

    @CallSuper fun onFailed() { logMethod() }
  }

  fun requestImageProcessing(
      modelId: String,
      imageByteArray: ByteArray,
      callback: ImageRecognitionNetworkResult
  )

}