package com.android.szparag.saymyname.presenters.contracts

import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Model
import com.android.szparag.saymyname.retrofit.models.translation.TranslatedText
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface CameraPresenter : Presenter<RealtimeCameraPreviewView> {

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
  fun onCameraPreviewViewInitialized() //todo: is it needed?
  fun onCameraPreviewViewInitializationFailed()

  fun initializeTextToSpeechClient()

  fun startCameraRealtimePreview()

  fun onUserTakePictureButtonClicked()
  fun takeCameraPicture()
  fun onCameraPhotoTaken()
  fun onCameraPhotoByteArrayReady(photoByteArray: ByteArray)
  //todo: what if processing fails? add methods


}