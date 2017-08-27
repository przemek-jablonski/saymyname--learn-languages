package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.ImageProcessingPresenter

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface ImageRecognitionModel : Model {

  fun requestImageProcessing(modelId: String, imageByteArray: ByteArray)
  fun attach(presenter: ImageProcessingPresenter)

}