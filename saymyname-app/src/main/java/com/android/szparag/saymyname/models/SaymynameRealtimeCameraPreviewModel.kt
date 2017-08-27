package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
class SaymynameRealtimeCameraPreviewModel(
    val imageRecognitionService: ImageRecognitionNetworkService,
    val translationService: TranslationNetworkService,
    val repository: ImagesWordsRepository
) : RealtimeCameraPreviewModel {

  private val networkSubscriptions: CompositeDisposable by lazy { CompositeDisposable() }

  override fun attach(): Completable {
    return repository.attach()
  }

  override fun detach(): Completable {
    return Completable.fromAction {
      networkSubscriptions.clear()
    }.andThen {
      repository.detach()
    }
  }

  override fun observeNewWords(): Flowable<Image> {
    return repository.fetchAllImages()
        .skip(1)
        .filter { list -> list.isNotEmpty() }
        .map { images -> images[0] }
  }

  override fun requestImageProcessingWithTranslation(modelId: String, imageByteArray: ByteArray?,
      languageTo: Int, languageFrom: Int,
      languagePair: String): Completable {
    imageByteArray ?: throw Throwable()
    return imageRecognitionService
        .requestImageProcessing(modelId, imageByteArray)
        .observeOn(Schedulers.io())
        .map { concepts -> concepts.map { concept -> concept.name }.subList(0, 3) }
        .flatMap { translationService.requestTextTranslation(it, languagePair) }
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable { words ->
          repository.pushImage(
              imageByteArray, languageFrom, languageTo, modelId,
              words.map { (first) -> first }, words.map { words -> words.second }
          )
        }
  }

}
