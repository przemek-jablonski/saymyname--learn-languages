package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import io.reactivex.Completable
import io.reactivex.Flowable
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
    repository.attach()
  }

  override fun detach() {
    repository.detach()
    networkSubscriptions.clear()
  }

  override fun observeNewWords(): Flowable<Image> {
    return repository.fetchLastImage().skip(1)
  }


  override fun requestImageProcessingWithTranslation(
      modelId: String, imageByteArray: ByteArray, languageTo: Int, languageFrom: Int,
      languagePair: String)
      : Completable {
    requestImageProcessing(modelId, imageByteArray, languageTo, languageFrom).toObservable<Unit>()

  }

  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int): Completable {
    return Completable.create {
      imageRecognitionService.requestImageProcessing(model = modelId, image = imageByteArray)
          .map { it.map { it -> it.name }.subList(0, 3) }
          .subscribeBy(
              onNext = {
                repository.pushImage(imageByteArray, languageFrom, languageTo, modelId).subscribe()
                repository.pushWordsOriginal(it).subscribe()
              },
              onError = {
                //todo: tutej jakies eventbusowanie ON_NETWORK_ERROR
              }
          )
    }
  }

  override fun requestTranslation(languagePair: String,
      textsToTranslate: List<String>): Completable {
    return Completable.create {
      translationService.requestTextTranslation(texts = textsToTranslate,
          languagePair = languagePair)
          .subscribeBy(
              onNext = { repository.pushWordsTranslated(it) },
              onError = {
                //todo: tutej jakies eventbusowanie ON_NETWORK_ERROR
              }
          )
    }
  }
}
