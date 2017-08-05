package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.services.contracts.NetworkService

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
class SaymynameRealtimeCameraPreviewModel(override val service: NetworkService) : RealtimeCameraPreviewModel {

  override fun attach(presenter: ImageProcessingPresenter) {

  }

  override fun requestTranslation(languagePair: String, textsToTranslate: List<String>) {

  }

  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray) {

  }


}