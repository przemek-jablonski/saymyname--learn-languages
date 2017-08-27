package com.android.szparag.saymyname.views.contracts

import com.android.szparag.saymyname.events.CameraPictureEvent
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/3/2017.
 */
interface RealtimeCameraPreviewView : View {

  fun renderRealtimeCameraPreview()
  fun stopRenderingRealtimeCameraPreview()
  fun renderLoadingAnimation()
  fun stopRenderingLoadingAnimation()

  fun renderNonTranslatedWords(nonTranslatedWords : List<String>)
  fun renderTranslatedWords(translatedWords : List<String>)
  fun stopRenderingWords()


  fun retrieveHardwareBackCamera()
  fun takePicture(): Observable<CameraPictureEvent>

  fun initializeCameraPreviewSurfaceView()

  fun initializeTextToSpeechClient()
  fun speakText(textToSpeak : String, flushSpeakingQueue : Boolean = false)

  //todo: this is unimplemented yet
  fun initializeSuddenMovementDetection()
  fun onSuddenMovementDetected()
  fun scaleCompressEncodePictureByteArray(pictureByteArray: ByteArray) : Observable<CameraPictureEvent>
  fun onUserTakePictureButtonClicked(): Observable<Any>

}