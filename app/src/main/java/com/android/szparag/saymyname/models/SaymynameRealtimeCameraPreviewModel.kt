package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC
import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionNetworkResult
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationNetworkResult
import com.android.szparag.saymyname.utils.DataCallback

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
class SaymynameRealtimeCameraPreviewModel(
    val imageRecognitionService: ImageRecognitionNetworkService,
    val translationService: TranslationNetworkService,
    val repository: ImagesWordsRepository
) : RealtimeCameraPreviewModel {

  private lateinit var presenter: RealtimeCameraPreviewPresenter

  override fun attach(presenter: RealtimeCameraPreviewPresenter) {
    this.presenter = presenter
    repository.create()
  }

  override fun detach() {
    repository.destroy()
  }

  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int) {
    repository.pushImage(imageByteArray, languageFrom, languageTo, modelId)
    imageRecognitionService.requestImageProcessing(
        modelId = modelId,
        imageByteArray = imageByteArray,
        callback = object : ImageRecognitionNetworkResult {
          override fun onSucceeded(concepts: List<Concept>) {
            super.onSucceeded(concepts)
            repository.pushWordsOriginal(
                wordsOriginal = concepts
                    .map { it -> it.name }
                    .filterNot {
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
                    }
                    .subList(0, 3)
                    .toTypedArray(),
                callback = object : DataCallback<Image> {
                  override fun onChange(data: Image) {
                    presenter.onImageVisionDataReceived(data.words.map { it -> it.original })
                  }
                })
          }

          //todo: in onFailed there should be argument with type of error!
          // todo: and the class should not be 'network request status' but 'model status', because realm can throw an error as well
          override fun onFailed() {
            super.onFailed()
            presenter.onImageVisionDataFailed(FAILURE_GENERIC)
          }
        })
  }

  override fun requestTranslation(languagePair: String, textsToTranslate: List<String>) {
    translationService.requestTextTranslation(
        languagesPair = languagePair,
        textsToTranslate = textsToTranslate,
        callback = object : TranslationNetworkResult {
          override fun onSucceeded(translatedTexts: List<String>) {
            super.onSucceeded(translatedTexts)
            repository.pushWordsTranslated(
                wordsTranslated = translatedTexts.toTypedArray(),
                callback = object : DataCallback<Image> {
                  override fun onChange(data: Image) {
                    presenter.onTranslationDataReceived(data.words.map { it -> it.translated })
                  }
                })
          }

          override fun onFailed() {
            super.onFailed()
            presenter.onTranslationDataFailed(FAILURE_GENERIC)
          }
        })
  }


}