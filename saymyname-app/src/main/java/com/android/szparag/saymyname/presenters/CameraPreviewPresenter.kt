package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.views.contracts.View
import java.util.Locale

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface CameraPreviewPresenter<V : View> : Presenter<V> {

  //todo: move it to model
  //todo: change to Int, not Enum
  //todo: add string with details from the server (or not?)


  override fun onViewReady()

  fun initializeCameraPreviewView()
  fun onCameraSetupFailed(exc: Throwable)
  fun initializeTextToSpeechClient(locale: Locale)
  fun startCameraRealtimePreview()
}