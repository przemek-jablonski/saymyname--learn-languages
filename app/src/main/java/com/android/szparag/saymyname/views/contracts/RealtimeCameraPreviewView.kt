package com.android.szparag.saymyname.views.contracts

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
  fun takePicture()
  fun scaleCompressPictureByteData(shortestResolutionDimension : Int)
  fun onTakePictureShutterTriggered()
  fun onTakePictureByteDataReady(pictureDataArray : ByteArray) //todo: this should be in presenter?
  fun onScaledCompressedPicgureByteDataReady(pictureDataArray: ByteArray)

  fun initializeCameraPreviewSurfaceView()

  fun initializeTextToSpeechClient()
  fun speakText(textToSpeak : String, flushSpeakingQueue : Boolean = false)

  //todo: this is unimplemented yet
  fun initializeSuddenMovementDetection()
  fun onSuddenMovementDetected()

}