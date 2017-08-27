package com.android.szparag.saymyname.views.contracts

import com.android.szparag.saymyname.events.CameraPictureEvent
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.Locale

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/3/2017.
 */
interface RealtimeCameraPreviewView : View {

  //user interface events:
  fun onUserTakePictureButtonClicked(): Observable<Any>
  fun onUserModelSwitchButtonClicked(): Observable<Any>
  fun onUserModelSwitchLanguageClicked(): Observable<Any>
  fun onUserHamburgerMenuClicked(): Observable<Any>

  //general view rendering:
  fun renderRealtimeCameraPreview(): Completable
  fun renderLoadingAnimation()
  fun stopRenderingRealtimeCameraPreview()
  fun stopRenderingLoadingAnimation()

  //words rendering:
  fun renderNonTranslatedWords(nonTranslatedWords: List<String>)
  fun renderTranslatedWords(translatedWords: List<String>)
  fun stopRenderingWords()

  //camera and taking photos:
  fun retrieveHardwareBackCamera(): Completable
  fun initializeCameraPreviewSurfaceView(): Completable
  fun scaleCompressEncodePictureByteArray(
      pictureByteArray: ByteArray): Observable<CameraPictureEvent>
  fun takePicture(): Observable<CameraPictureEvent>

  //text-to-speech:
  fun initializeTextToSpeechClient(locale: Locale)
  fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean = false)


  //todo: this is unimplemented yet
  fun initializeSuddenMovementDetection()
  fun onSuddenMovementDetected()

}