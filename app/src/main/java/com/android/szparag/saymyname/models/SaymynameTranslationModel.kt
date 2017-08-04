package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.CameraPresenter
import com.android.szparag.saymyname.presenters.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
class SaymynameTranslationModel(override val service : TranslationNetworkService) : TranslationModel {

  private lateinit var presenter : ImageProcessingPresenter

  override fun attach(presenter: ImageProcessingPresenter) {
    com.android.szparag.saymyname.utils.logMethod()
    this.presenter = presenter
  }

  override fun requestTranslation(languagePair: String, textsToTranslate: List<String>) {
    com.android.szparag.saymyname.utils.logMethod()
    service.requestTextTranslation(textsToTranslate, languagePair, object : TranslationNetworkService.TranslationNetworkResult {
      override fun onSucceeded(translatedTexts: List<String>) {
        super.onSucceeded(translatedTexts)
        presenter?.onTranslationDataReceived(translatedTexts)
      }

      override fun onFailed() {
        super.onFailed()
        presenter?.onTranslationDataFailed(
            CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC)
      }
    })
  }
}