package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.entities.Image
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
interface RealtimeCameraPreviewModel {
  //todo: make parent interface (Model) with generic attach function

  //todo: make this generic
  fun attach(presenter: RealtimeCameraPreviewPresenter)

  fun detach()

  fun observeNewWords() : Flowable<Image>

  fun requestImageProcessingWithTranslation(modelId: String, imageByteArray: ByteArray?, languageTo: Int,
      languageFrom: Int, languagePair: String) : Observable<Image>

  //todo: change arguments order so that they match rest of the model (like in SMNRealtimeCameraPreviewModel)
  fun requestImageProcessing(modelId: String, imageByteArray: ByteArray?, languageTo: Int,
      languageFrom: Int): Observable<Image>

  fun requestTranslation(languagePair: String, textsToTranslate: List<String>): Observable<Image>
}