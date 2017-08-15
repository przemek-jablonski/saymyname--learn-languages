package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.entities.Image
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
interface RealtimeCameraPreviewModel {
  //todo: make parent interface (Model) with generic attach function

  //todo: make this generic
  fun attach(presenter: RealtimeCameraPreviewPresenter)

  fun detach()

  fun observeNewWords() : Flowable<Image>

  fun requestImageProcessingWithTranslation(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int, languagePair: String) : Completable

  fun requestImageProcessing(modelId: String, imageByteArray: ByteArray, languageTo: Int,
      languageFrom: Int): Completable

  fun requestTranslation(languagePair: String, textsToTranslate: List<String>): Completable
}