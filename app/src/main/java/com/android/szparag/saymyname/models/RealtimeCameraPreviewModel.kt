package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
interface RealtimeCameraPreviewModel {
  //todo: make parent interface (Model) with generic attach function

  //todo: make this generic
  fun attach(presenter: RealtimeCameraPreviewPresenter)
  fun detach()

  fun requestImageProcessing(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int)

  fun requestTranslation(languagePair: String, textsToTranslate: List<String>)
}