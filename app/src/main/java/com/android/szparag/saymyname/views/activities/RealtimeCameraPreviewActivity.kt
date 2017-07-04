package com.android.szparag.saymyname.views.activities

import android.app.Activity
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.bindView
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import hugo.weaving.DebugLog
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION") //because of Camera1 API
@DebugLog
class RealtimeCameraPreviewActivity : AppCompatActivity(), RealtimeCameraPreviewView, Callback {

  val cameraSurfaceView:SurfaceView by bindView(R.id.surfaceview_realtime_camera)

  private lateinit var textToSpeechClient : TextToSpeech
  private var cameraInstance : Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_realtime_camera_preview)
  }

  override fun renderRealtimeCameraPreview() {
    cameraInstance?.let {
      try {
        cameraInstance.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
          //todo: get rid of cameraId, we only care about back-facing cam here
          setCameraDisplayOrientation(this@RealtimeCameraPreviewActivity, 0, it)
          setFocusMode(cameraInstance)
        }
      } catch (exc: IOException) {
        //todo: if exception, CALL PRESENTER
        exc.printStackTrace()
        return
      }

      it.startPreview()
    }
  }

  private fun setCameraDisplayOrientation(activity: Activity, cameraId: Int,
      camera: android.hardware.Camera) {
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)
    val parameters = camera.parameters;
    val rotation = activity.windowManager.defaultDisplay.rotation
    var degrees = 0
    when (rotation) {
      Surface.ROTATION_0 -> degrees = 0
      Surface.ROTATION_90 -> degrees = 90
      Surface.ROTATION_180 -> degrees = 180
      Surface.ROTATION_270 -> degrees = 270
    }

    var result: Int
    //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    // do something for phones running an SDK before lollipop
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360
      result = (360 - result) % 360 // compensate the mirror
    } else { // back-facing
      result = (info.orientation - degrees + 360) % 360
    }

    parameters.setRotation(result)
    camera.parameters = parameters
    camera.setDisplayOrientation(result)
  }

  private fun setFocusMode(cameraInstance: Camera?) {
    //todo: implement system that handles case where cam doesnt have this FocusMode
    cameraInstance?.let {
      val parameters = it.parameters
      parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
      it.parameters = parameters
    }
  }

  override fun initializeCameraPreviewSurfaceView() {
    val holder = cameraSurfaceView.holder
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  override fun stopRenderingRealtimeCameraPreview() {

  }

  override fun renderLoadingAnimation() {//...
  }

  override fun stopRenderingLoadingAnimation() {//...
  }

  override fun renderNonTranslatedWords(nonTranslatedWords: List<String>) {
    //...
  }

  override fun renderTranslatedWords(translatedWords: List<String>) {
    //...
  }

  override fun stopRenderingWords() {
    //...
  }

  override fun retrieveHardwareBackCamera() {
    //todo: if opening camera failed (or succeeded), then CALL PRESENTER!
    cameraInstance = openHardwareBackCamera()
  }

  private fun openHardwareBackCamera() : Camera? {
    try {
      return Camera.open()
    } catch (exc: RuntimeException) {
      //todo: ...logging, show error, whatever
      exc.printStackTrace()
    }
    return null
  }

  override fun takePicture() {//...
  }

  override fun scaleCompressPictureByteData(shortestResolutionDimension: Int) {//...
  }

  override fun onTakePictureShutterTriggered() {//...
  }

  override fun onTakePictureByteDataReady(pictureDataArray: ByteArray) {//...
  }

  override fun onScaledCompressedPicgureByteDataReady(pictureDataArray: ByteArray) {//...
  }

  override fun initializeTextToSpeechClient() {
    textToSpeechClient = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
      status: Int ->
      status.takeIf { it != TextToSpeech.ERROR }?.run {
        textToSpeechClient.language = Locale.UK
      }
    })
  }

  override fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean) {//...
  }


  override fun initializeSuddenMovementDetection() {//...
  }

  override fun onSuddenMovementDetected() {//...
  }



  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {//...
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {//...
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {//...
  }
}
