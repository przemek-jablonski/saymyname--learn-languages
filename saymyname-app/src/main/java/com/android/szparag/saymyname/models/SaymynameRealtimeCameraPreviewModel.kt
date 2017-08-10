package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC
import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
class SaymynameRealtimeCameraPreviewModel(
    val imageRecognitionService: ImageRecognitionNetworkService,
    val translationService: TranslationNetworkService,
    val repository: ImagesWordsRepository
) : RealtimeCameraPreviewModel {

  private lateinit var presenter: RealtimeCameraPreviewPresenter
  private val networkSubscriptions: CompositeDisposable by lazy { CompositeDisposable() }

  override fun attach(presenter: RealtimeCameraPreviewPresenter) {
    this.presenter = presenter
    repository.create()
  }

  override fun detach() {
    repository.destroy()
    networkSubscriptions.clear()
  }

  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int) {
    repository.pushImage(imageByteArray, languageFrom, languageTo, modelId)
    imageRecognitionService.requestImageProcessing(model = modelId, image = imageByteArray)
        .map { conceptList ->
          conceptList.map { it -> it.name }.filterNot {
            it == ("no person") ||
                it == "horizontal" ||
                it == ("vertical") ||
                it == ("control") ||
                it == ("offense") ||
                it == ("one") ||
                it == ("two") ||
                it == ("container") ||
                it == ("abstract") ||
                it == ("Luna") ||
                it == ("crescent") ||
                it == ("background") ||
                it == ("insubstantial")
          }.subList(0, 3)
        }.subscribeBy(
        onNext = {
          logMethod()
          if (it != null)
            presenter.onImageVisionDataReceived(
                it) //todo: refactor that so that is passing observable to presenter and subscribe there
          else
            presenter.onImageVisionDataFailed(FAILURE_GENERIC)
        },
        onError = {
          logMethod()
          presenter.onImageVisionDataFailed(FAILURE_GENERIC)
        }
    )
  }

  override fun requestTranslation(languagePair: String, textsToTranslate: List<String>) {
    translationService.requestTextTranslation(texts = textsToTranslate, languagePair = languagePair)
        .subscribeBy(
            onNext = {
              translatedList ->
              presenter.onTranslationDataReceived(translatedList)
            },
            onError = {
              presenter.onTranslationDataFailed(FAILURE_GENERIC)
            }
        )
  }
}
