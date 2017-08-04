package com.android.szparag.saymyname.models.contracts

import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC
import com.android.szparag.saymyname.presenters.contracts.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationNetworkResult
import com.android.szparag.saymyname.utils.logMethod

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
class SaymynameTranslationModel(override val service : TranslationNetworkService) : TranslationModel{

  private lateinit var presenter : ImageProcessingPresenter

  override fun attach(presenter: ImageProcessingPresenter) {
    logMethod()
    this.presenter = presenter
  }

  override fun requestTranslation(languagePair: String, textsToTranslate: List<String>) {
    logMethod()
    service.requestTextTranslation(textsToTranslate, languagePair, object : TranslationNetworkResult {
      override fun onSucceeded(translatedTexts: List<String>) {
        super.onSucceeded(translatedTexts)
        presenter?.onTranslationDataReceived(translatedTexts)
      }

      override fun onFailed() {
        super.onFailed()
        presenter?.onTranslationDataFailed(FAILURE_GENERIC)
      }
    })
  }
}