package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.CLOTHING
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.COLOURS
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.FOOD
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.GENERAL
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel.TRAVEL
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.ENGLISH
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.GERMAN
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.ITALIAN
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.POLISH
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationLanguage.SPANISH
import com.android.szparag.saymyname.utils.ERROR_IMAGEPROCESSINGWITHTRANSLATION_IMAGE_NULL
import com.android.szparag.saymyname.utils.Logger
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
    private val imageRecognitionService: ImageRecognitionNetworkService,
    private val translationService: TranslationNetworkService,
    private val repository: ImagesWordsRepository
) : RealtimeCameraPreviewModel {

  private val logger = Logger.create(SaymynameRealtimeCameraPreviewModel::class)
  private val networkSubscriptions: CompositeDisposable by lazy(::CompositeDisposable)

  override fun attach(): Completable = repository.attach()

  override fun detach(): Completable = Completable.fromAction(networkSubscriptions::clear).andThen {
    repository.detach()
  }

  override fun observeNewWords(): Flowable<Image> = repository.fetchAllImages()
      .skip(1)
      .filter { list -> list.isNotEmpty() }
      .map { images -> images[0] }

  override fun requestImageProcessingWithTranslation(
      modelString: String,
      imageByteArray: ByteArray?,
      languageFromCode: String, languageToString: String): Observable<Image> {
    imageByteArray ?: throw ERROR_IMAGEPROCESSINGWITHTRANSLATION_IMAGE_NULL
    val modelType = modelStringToType(modelString)
    val languageToType = languageStringToType(languageToString)
    logger.debug("requestImageProcessingWithTranslation, modelType: $modelType, languageFromCode: $languageFromCode" +
        ", languageToType: $languageToType, imageByteArray: ${imageByteArray.hashCode()}")
    return imageRecognitionService
        .requestImageProcessing(modelType.modelId, imageByteArray)
        .observeOn(Schedulers.io())
        .map { concepts ->
          concepts.map(Concept::name).subList(0, 3)
        }
        .flatMap { concepts ->
          translationService.requestTextTranslation(concepts,
              languageCodesToPair(languageFromCode, languageToType.languageCode))
        }
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap { words ->
          repository.pushImage(imageByteArray, languageToString, languageToType.languageCode, modelType.modelString,
              words.map { (first) -> first }, words.map(Pair<String, String>::second))
        }
  }

  private fun modelStringToType(modelString: String) = when (modelString) {
    COLOURS.modelString  -> COLOURS
    FOOD.modelString     -> FOOD
    TRAVEL.modelString   -> TRAVEL
    CLOTHING.modelString -> CLOTHING
    else                 -> GENERAL
  }

  private fun languageStringToType(languageString: String) = when (languageString) {
    ITALIAN.languageString -> ITALIAN
    SPANISH.languageString -> SPANISH
    GERMAN.languageString  -> GERMAN
    ENGLISH.languageString -> ENGLISH
    POLISH.languageString  -> POLISH
    else                   -> throw RuntimeException("not supported language")
  }

  private fun languageCodesToPair(languageFromCode: String, languageToCode: String): String =
      "$languageFromCode-$languageToCode"
}
