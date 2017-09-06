package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.repositories.entities.Image
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
interface RealtimeCameraPreviewModel {

  enum class NetworkRequestStatus {
    OK,
    MIXED_SUCCESS,
    FAILURE_GENERIC,
    REQUEST_LIMIT_EXCEEDED_GENERIC,
    REQUEST_LIMIT_EXCEEDED_HOURLY,
    REQUEST_LIMIT_EXCEEDED_MONTHLY,
    SERVER_NOT_AVAILABLE,
    INVALID_REQUEST,
    FETCHING_FAILED,
    INTERNAL_SERVER_ERROR,
    SERVER_LOGIC_ERROR,
    INVALID_CREDENTIALS_GENERIC,
    INVALID_CREDENTIALS_SCOPE,
    INVALID_CREDENTIALS_KEY
  }

  fun attach(): Completable

  fun detach(): Completable

  fun observeNewWords(): Flowable<Image>

  //  //todo: change arguments order so that they match rest of the model (like in SMNRealtimeCameraPreviewModel)
  fun requestImageProcessingWithTranslation(
      modelString: String,
      imageByteArray: ByteArray?,
      languageFromCode: String, languageToString: String): Completable

}