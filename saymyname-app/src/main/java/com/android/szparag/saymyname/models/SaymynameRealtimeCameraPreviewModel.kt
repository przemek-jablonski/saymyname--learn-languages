package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.*
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.*
import com.android.szparag.saymyname.utils.logMethod
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

  override fun requestImageProcessingWithTranslation(
      modelString: String,
      imageByteArray: ByteArray?,
      languageFromCode: String, languageToString: String): Observable<Image> {
    imageByteArray ?: throw Throwable()
    val modelType = modelStringToType(modelString)
    val languageToType = languageStringToType(languageToString)
    logMethod("modelType: $modelType, languageFromCode: $languageFromCode, languageToType: $languageToType, imageByteArray: ${imageByteArray.hashCode()}")
    return imageRecognitionService
        .requestImageProcessing(modelType.modelId, imageByteArray)
        .observeOn(Schedulers.io())
        .map { concepts ->
          concepts.map { concept -> concept.name }.subList(0, 3)
        }
        .flatMap { concepts ->
          translationService.requestTextTranslation(concepts, languageCodesToPair(languageFromCode, languageToType.languageCode))
        }
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { words ->
          repository.pushImage(imageByteArray, languageToString, languageToType.languageCode, modelType.modelString, words.map { (first) -> first }, words.map { words -> words.second })
        }
  }

  private fun modelStringToType(modelString: String): ImageRecognitionModel {
    when(modelString) {
      COLOURS.modelString -> return COLOURS
      FOOD.modelString -> return FOOD
      TRAVEL.modelString -> return TRAVEL
      CLOTHING.modelString -> return CLOTHING
      else -> return GENERAL
    }
  }

  private fun languageStringToType(languageString: String): TranslationLanguage {
    when(languageString) {
      ITALIAN.languageString -> return ITALIAN
      SPANISH.languageString -> return SPANISH
      GERMAN.languageString -> return GERMAN
      ENGLISH.languageString -> return ENGLISH
      POLISH.languageString -> return POLISH
      else -> throw RuntimeException("not supported language")
    }
  }

  private fun languageCodesToPair(languageFromCode: String, languageToCode: String): String {
    return "$languageFromCode-$languageToCode"
  }
}
