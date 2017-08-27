package com.android.szparag.saymyname.presenters

import java.util.Locale

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface CameraPresenter : Presenter {

  //todo: move it to model
  //todo: change to Int, not Enum
  //todo: add string with details from the server (or not?)
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

  override fun onViewReady()

  fun initializeCameraPreviewView()
  fun onCameraSetupFailed(exc: Throwable)

  fun initializeTextToSpeechClient(locale: Locale)

  fun startCameraRealtimePreview()

  //todo: what if camera image processing fails? add methods


}