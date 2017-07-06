package com.android.szparag.saymyname.views.activities

import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.bindView
import com.android.szparag.saymyname.dagger.DaggerWrapper
import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter
import com.android.szparag.saymyname.presenters.contracts.RealtimeCameraPresenter
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.widgets.SaymynameFloatingWordsView
import com.android.szparag.saymyname.views.widgets.contracts.FloatingWordsView
import hugo.weaving.DebugLog
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION") //because of Camera1 API
@DebugLog
class RealtimeCameraPreviewActivity : AppCompatActivity(), RealtimeCameraPreviewView, Callback {

  val cameraSurfaceView: SurfaceView by bindView(R.id.surfaceview_realtime_camera)
  val buttonHamburgerMenu: Button by bindView(R.id.button_menu_hamburger)
  val buttonSwitchLanguage: Button by bindView(R.id.button_switch_language)
  val buttonSwitchModel: Button by bindView(R.id.button_switch_model)
  val buttonCameraShutter: Button by bindView(R.id.button_shutter)
  val floatingWordsView : SaymynameFloatingWordsView by bindView(R.id.view_floating_words) //todo: refactor so that there is only interface here

  private lateinit var textToSpeechClient: TextToSpeech
  @Inject lateinit var presenter: RealtimeCameraPresenter //todo: remove ? later on, VERY IMPORTANT!

  private var cameraInstance: Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    logMethod()
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_realtime_camera_preview)
  }

  override fun onStart() {
    logMethod()
    super.onStart()
    DaggerWrapper.getComponent(this).inject(this)
    setupViews()
    presenter?.attach(this)
  }

  override fun onResume() {
    logMethod()
    super.onResume()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    logMethod()
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) presenter.onViewReady()

  }

  override fun onPause() {
    logMethod()
    super.onPause()
  }

  override fun onStop() {
    logMethod()
    presenter.detach()
    super.onStop()
  }

  override fun onDestroy() {
    logMethod()
    super.onDestroy()
  }

  override fun setupViews() {
    logMethod()
    buttonCameraShutter.setOnClickListener { presenter.onUserTakePictureButtonClicked() }
  }

  override fun initializeCameraPreviewSurfaceView() {
    logMethod()
    val holder = cameraSurfaceView.holder
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  override fun retrieveHardwareBackCamera() {
    logMethod()
    //todo: if opening camera failed (or succeeded), then CALL PRESENTER!
    cameraInstance = openHardwareBackCamera()
    if (cameraInstance == null)
      presenter.onCameraPreviewViewInitializationFailed()
  }

  override fun renderRealtimeCameraPreview() {
    logMethod()
    cameraInstance?.let {
      try {
        cameraInstance.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
          configureCameraDisplayOrientation(0)
          configureFocusMode(cameraInstance)
          it.startPreview()
          presenter.onCameraPreviewViewInitialized()
        }
      } catch (exc: IOException) {
        presenter.onCameraPreviewViewInitializationFailed()
        exc.printStackTrace()
        return
      }
    }
  }

  //todo: get rid of cameraId, we only care about back-facing cam here
  private fun configureCameraDisplayOrientation(cameraId: Int) {
    logMethod()
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
    logMethod()
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)
    return info
  }

  private fun configureFocusMode(cameraInstance: Camera?) {
    logMethod()
    cameraInstance?.let {
      //todo: implement system that handles case where cam doesnt have this FocusMode
      val parameters = it.parameters
      parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
      it.parameters = parameters
    }
  }


  override fun stopRenderingRealtimeCameraPreview() {
    logMethod()

  }

  override fun renderLoadingAnimation() {
    logMethod()
  }

  override fun stopRenderingLoadingAnimation() {
    logMethod()
  }

  override fun renderNonTranslatedWords(nonTranslatedWords: List<String>) {
    logMethod()
    floatingWordsView.renderAuxiliaryWords(nonTranslatedWords)
  }

  override fun renderTranslatedWords(translatedWords: List<String>) {
    logMethod()
    floatingWordsView.renderPrimaryWords(translatedWords)
  }

  override fun stopRenderingWords() {
    logMethod()
    //...
  }


  private fun openHardwareBackCamera(): Camera? {
    logMethod()
    try {
      return Camera.open()
    } catch (exc: RuntimeException) {
      //todo: ...logging, show error, whatever
      exc.printStackTrace()
    }
    return null
  }

  override fun takePicture() {
    logMethod()
    cameraInstance?.takePicture(
        Camera.ShutterCallback { presenter.onCameraPhotoTaken() },
        null,
        Camera.PictureCallback { data, camera ->
          presenter.onCameraPhotoByteArrayReady(data)
          cameraInstance?.startPreview()
        }
    )
  }

  override fun scaleCompressEncodePictureByteArray(pictureByteArray: ByteArray) {
    val options = BitmapFactory.Options().apply {
      //      this.inJustDecodeBounds = true
      this.inPurgeable = true
      //todo: refactor so that i can specify minimum res (600-720px) instead of scaling
      //todo: because i do not know how powerful user camera is
      rescaleImageRequestFactor(8, this)
    }
    val originalByteStream = pictureByteArray
    val originalBitmap = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size)
    val scaledBitmap = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size,
        options)
    val compressedByteStream = ByteArrayOutputStream()
    scaledBitmap.compress(JPEG, 60, compressedByteStream)
    val compressedBitmap = BitmapFactory.decodeByteArray(compressedByteStream.toByteArray(), 0,
        compressedByteStream.toByteArray().size)

//    compressedPictureBitmap = compressedBitmap
//    compressedPictureByteArray = compressedByteStream.toByteArray()
      presenter.onCameraCompressedPhotoByteArrayReady(compressedByteStream.toByteArray())

//    Bitmap.createScaledBitmap()
//    options.inPurgeable = false

//    (findViewById(R.id.imageView) as ImageView).setImageBitmap(compressedBitmap)
//    findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
//    findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
  }

  //todo: this kotlin syntax here really sucks, refactor!
  private fun rescaleImageRequestFactor(downScaleFactor: Int,
      bitmapOptions: BitmapFactory.Options): BitmapFactory.Options {
    bitmapOptions.inSampleSize = downScaleFactor
    return bitmapOptions
  }


  override fun initializeTextToSpeechClient() {
    logMethod()
    textToSpeechClient = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
      status: Int ->
      status.takeIf { it != TextToSpeech.ERROR }?.run {
        textToSpeechClient.language = Locale.UK
      }
    })
  }

  override fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean) {
    logMethod()
    textToSpeechClient.speak(
        textToSpeak,
        if (flushSpeakingQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD,
        null)
  }


  override fun initializeSuddenMovementDetection() {
    logMethod()
  }

  override fun onSuddenMovementDetected() {
    logMethod()
  }


  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    logMethod()
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    logMethod()
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    logMethod()
  }
}
