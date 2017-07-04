package com.android.szparag.saymyname.views.activities

import android.hardware.Camera
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView
import android.widget.Button
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.bindView
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import hugo.weaving.DebugLog
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION") //because of Camera1 API
@DebugLog
class RealtimeCameraPreviewActivity : AppCompatActivity(), RealtimeCameraPreviewView, Callback {

  val cameraSurfaceView: SurfaceView by bindView(R.id.surfaceview_realtime_camera)
  val buttonHamburgerMenu: Button by bindView(R.id.button_menu_hamburger)
  val buttonSwitchLanguage: Button by bindView(R.id.button_switch_language)
  val buttonSwitchModel: Button by bindView(R.id.button_switch_model)
  val buttonCameraShutter: Button by bindView(R.id.button_shutter)

  private lateinit var textToSpeechClient: TextToSpeech
  private var presenter: CameraPresenter? = null //todo: remove ? later on, VERY IMPORTANT!

  private var cameraInstance: Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_realtime_camera_preview)
  }

  override fun onStart() {
    super.onStart()
    setupViews()
    presenter?.attach(this)
  }

  override fun onResume() {
    super.onResume()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus)
      presenter?.onViewReady()

  }

  override fun onPause() {
    super.onPause()
  }

  override fun onStop() {
    presenter?.detach()
    super.onStop()
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  override fun setupViews() {
    buttonCameraShutter.setOnClickListener { presenter?.onUserTakePictureButtonClicked() }
  }

  override fun initializeCameraPreviewSurfaceView() {
    val holder = cameraSurfaceView.holder
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  override fun retrieveHardwareBackCamera() {
    //todo: if opening camera failed (or succeeded), then CALL PRESENTER!
    cameraInstance = openHardwareBackCamera()
    if (cameraInstance == null)
      presenter?.onCameraPreviewViewInitializationFailed()
  }

  override fun renderRealtimeCameraPreview() {
    cameraInstance?.let {
      try {
        cameraInstance.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
          configureCameraDisplayOrientation(0)
          configureFocusMode(cameraInstance)
          it.startPreview()
          presenter?.onCameraPreviewViewInitialized()
        }
      } catch (exc: IOException) {
        presenter?.onCameraPreviewViewInitializationFailed()
        exc.printStackTrace()
        return
      }
    }
  }

  //todo: get rid of cameraId, we only care about back-facing cam here
  private fun configureCameraDisplayOrientation(cameraId: Int) {
    cameraInstance?.let {
      val info = getCameraHardwareInfo(cameraId)
      val parameters = it.parameters
      val displayRotation = this.windowManager.defaultDisplay.rotation
      var degreesToRotate = 0
      when (displayRotation) {
        Surface.ROTATION_0 -> degreesToRotate = 0
        Surface.ROTATION_90 -> degreesToRotate = 90
        Surface.ROTATION_180 -> degreesToRotate = 180
        Surface.ROTATION_270 -> degreesToRotate = 270
      }

      //todo: add link to this answer (from so, duh)
      var degreesToRotateFinal: Int
      if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        degreesToRotateFinal = (360 - (info.orientation + degreesToRotate) % 360) % 360 //super haxxxx
      } else {
        degreesToRotateFinal = (info.orientation - degreesToRotate + 360) % 360
      }

      parameters.setRotation(degreesToRotateFinal)
      it.parameters = parameters
      it.setDisplayOrientation(degreesToRotateFinal)
    }
  }

  private fun getCameraHardwareInfo(cameraId: Int): Camera.CameraInfo {
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)
    return info
  }

  private fun configureFocusMode(cameraInstance: Camera?) {
    cameraInstance?.let {
      //todo: implement system that handles case where cam doesnt have this FocusMode
      val parameters = it.parameters
      parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
      it.parameters = parameters
    }
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


  private fun openHardwareBackCamera(): Camera? {
    try {
      return Camera.open()
    } catch (exc: RuntimeException) {
      //todo: ...logging, show error, whatever
      exc.printStackTrace()
    }
    return null
  }

  override fun takePicture() {
    cameraInstance?.takePicture(
        Camera.ShutterCallback { presenter?.onCameraPhotoTaken() },
        null,
        Camera.PictureCallback { data, camera -> presenter?.onCameraPhotoByteArrayReady(data) }
    )
  }


  override fun initializeTextToSpeechClient() {
    textToSpeechClient = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
      status: Int ->
      status.takeIf { it != TextToSpeech.ERROR }?.run {
        textToSpeechClient.language = Locale.UK
      }
    })
  }

  override fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean) {
    textToSpeechClient.speak(
        textToSpeak,
        if (flushSpeakingQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
        null)
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
