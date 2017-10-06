package com.android.szparag.saymyname.views.contracts

import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/3/2017.
 */
interface RealtimeCameraPreviewView :
    CameraPreviewView,
    ImageRecognitionView,
    TextToSpeechView {

  //user interface events:
  fun onUserModelSwitchButtonClicked(): Observable<String>

  fun onUserLanguageSwitchClicked(): Observable<String>
  fun onUserHamburgerMenuClicked(): Observable<Any>
  fun onUserHistoricalEntriesClicked(): Observable<Any>

  //general view rendering:
  fun renderLoadingAnimation()

  fun stopRenderingLoadingAnimation()

  //bottomsheet behaviour:
  fun bottomSheetPeek()

  fun bottomSheetFillData(imageBytes: ByteArray, textsOriginal: List<String>, textsTranslated: List<String>,
      dateTime: Long)

  fun bottomSheetUnpeek()

  //camera parallax fx:
  fun initializeSuddenMovementDetection()

  fun onSuddenMovementDetected()

}