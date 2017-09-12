package com.android.szparag.saymyname.views.activities


import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.AppCompatImageButton
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.SurfaceView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.dagger.DaggerGlobalScopeWrapper
import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_SHUTTER_EVENT
import com.android.szparag.saymyname.events.CameraSurfaceEvent
import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.utils.ERROR_CAMERA_NATIVE_EXCEPTION
import com.android.szparag.saymyname.utils.ERROR_CAMERA_RENDERING_COMMAND_EXC
import com.android.szparag.saymyname.utils.ERROR_CAMERA_RENDERING_COMMAND_NULL
import com.android.szparag.saymyname.utils.ERROR_CAMERA_RETRIEVAL
import com.android.szparag.saymyname.utils.ERROR_COMPRESSED_BYTES_INVALID_SIZE
import com.android.szparag.saymyname.utils.Logger
import com.android.szparag.saymyname.utils.bindView
import com.android.szparag.saymyname.utils.configureCameraDisplayOrientation
import com.android.szparag.saymyname.utils.configureFocusMode
import com.android.szparag.saymyname.utils.createArrayAdapter
import com.android.szparag.saymyname.utils.getCameraHardwareInfo
import com.android.szparag.saymyname.utils.hide
import com.android.szparag.saymyname.utils.itemSelections
import com.android.szparag.saymyname.utils.letNull
import com.android.szparag.saymyname.utils.setRotation
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.contracts.View.UserAlertMessage
import com.android.szparag.saymyname.views.widgets.FullscreenMessageInfo
import com.android.szparag.saymyname.views.widgets.SaymynameCameraShutterButton
import com.android.szparag.saymyname.views.widgets.SaymynameCameraSurfaceView
import com.android.szparag.saymyname.views.widgets.overlays.BottomSheetSinglePhotoDetails
import com.android.szparag.saymyname.views.widgets.overlays.SaymynameFloatingWordsView
import com.jakewharton.rxbinding2.view.RxView
import hugo.weaving.DebugLog
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import java.io.ByteArrayOutputStream
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION") //because of Camera1 API
@DebugLog
class RealtimeCameraPreviewActivity : SaymynameBaseActivity<RealtimeCameraPreviewPresenter>(), RealtimeCameraPreviewView {

  val cameraSurfaceView: SaymynameCameraSurfaceView by bindView(R.id.surfaceview_realtime_camera)
  val buttonHamburgerMenu: AppCompatImageButton by bindView(R.id.button_menu_hamburger)
  val spinnerSwitchLanguage: Spinner by bindView(R.id.button_switch_language)
  lateinit var spinnerSwitchLanguageAdapter: ArrayAdapter<CharSequence>
  val spinnerSwitchModel: Spinner by bindView(R.id.button_switch_model)
  lateinit var spinnerSwitchModelAdapter: ArrayAdapter<CharSequence>
  val buttonHistoricalEntries: AppCompatImageButton by bindView(R.id.button_menu_charts)
  val buttonCameraShutter: SaymynameCameraShutterButton by bindView(
      R.id.button_shutter) //todo: refactor to just interface (CameraShutterButton)
  val floatingWordsView: SaymynameFloatingWordsView by bindView(
      R.id.view_floating_words) //todo: refactor so that there is only interface here
  val bottomSheetSinglePhotoDetails: BottomSheetSinglePhotoDetails by bindView(
      R.id.layout_single_photo_details)
  val fullscreenMessageInfo: FullscreenMessageInfo by bindView(R.id.fullscreen_message_info)
  var fullscreenMessageType: UserAlertMessage? = null
  private lateinit var bottomSheetBehavioursSinglePhotoDetails: BottomSheetBehavior<View>

  //cannot be injected because of a listener attached to constructor
  private lateinit var textToSpeechClient: TextToSpeech
  @Inject lateinit override var presenter: RealtimeCameraPreviewPresenter
  private var cameraInstance: Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    logger.debug("onCreate, bundle: $savedInstanceState")
    setContentView(R.layout.activity_realtime_camera_preview)
  }

  override fun onStart() {
    super.onStart()
    logger.debug("onStart")
    DaggerGlobalScopeWrapper.getComponent(this).inject(
        this) //todo: find a way to generize them in Kotlin
    presenter.attach(this) //todo: find a way to generize them in Kotlin

    spinnerSwitchLanguageAdapter = createArrayAdapter(R.array.spinner_lang_data)
    spinnerSwitchModelAdapter = createArrayAdapter(R.array.spinner_model_data)
    spinnerSwitchLanguage.adapter = spinnerSwitchLanguageAdapter
    spinnerSwitchModel.adapter = spinnerSwitchModelAdapter
    bottomSheetBehavioursSinglePhotoDetails = BottomSheetBehavior.from(
        bottomSheetSinglePhotoDetails)
    bottomSheetBehavioursSinglePhotoDetails.isHideable = false
    bottomSheetBehavioursSinglePhotoDetails.peekHeight = 0
    bottomSheetBehavioursSinglePhotoDetails.setBottomSheetCallback(object : BottomSheetCallback() {
      override fun onSlide(bottomSheet: View, slideOffset: Float) {

      }

      override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
          BottomSheetBehavior.STATE_COLLAPSED -> {
            logger.debug("STATE_COLLAPSED")
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
              bottomSheetSinglePhotoDetails.background = resources.getDrawable(
                  R.color.saymyname_blue_alpha_light)
            }
          }
          BottomSheetBehavior.STATE_SETTLING -> {
            logger.debug("STATE_SETTLING")
          }
          BottomSheetBehavior.STATE_HIDDEN -> {
            logger.debug("STATE_HIDDEN")
          }
          BottomSheetBehavior.STATE_EXPANDED -> {
            logger.debug("STATE_EXPANDED")
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
              bottomSheetSinglePhotoDetails.background = resources.getDrawable(
                  R.color.saymyname_blue_light)
            }
          }
          BottomSheetBehavior.STATE_DRAGGING -> {
            logger.debug("STATE_DRAGGING")
          }
        }
      }

    })

  }

  override fun onPause() {
    super.onPause()
    logger.debug("onPause")
    cameraInstance?.release()
    cameraInstance = null
  }

  override fun onStop() {
    presenter.detach()
    logger.debug("onStop")
    super.onStop()
  }


  override fun bottomSheetPeek() {
    logger.debug("bottomSheetPeek")
    bottomSheetBehavioursSinglePhotoDetails.peekHeight = 75
  }

  override fun bottomSheetFillData(imageBytes: ByteArray,
      textsOriginal: List<String>,
      textsTranslated: List<String>,
      dateTime: Long) {
    logger.debug("bottomSheetFillData, textsOriginal: $textsOriginal, textsTranslated: $textsTranslated, dateTime: $dateTime, imageBytes: ${imageBytes.hashCode()}")
    bottomSheetSinglePhotoDetails
        .setPhotoDetails(imageBytes, textsOriginal, textsTranslated, dateTime)
  }

  override fun bottomSheetUnpeek() {
    logger.debug("bottomSheetUnpeek")
    bottomSheetBehavioursSinglePhotoDetails.peekHeight = 0
  }


  override fun onUserTakePictureButtonClicked(): Observable<Any> {
    logger.debug("onUserTakePictureButtonClicked")
    return RxView.clicks(buttonCameraShutter)
  }

  override fun onUserModelSwitchButtonClicked(): Observable<String> {
    logger.debug("onUserModelSwitchButtonClicked")
    return itemSelections(spinnerSwitchModel)
  }

  override fun onUserLanguageSwitchClicked(): Observable<String> {
    logger.debug("onUserLanguageSwitchClicked")
    return itemSelections(spinnerSwitchLanguage)
  }

  override fun onUserHamburgerMenuClicked(): Observable<Any> {
    logger.debug("onUserHamburgerMenuClicked")
    return RxView
        .clicks(buttonHamburgerMenu)
        .doOnNext({ parentDrawerLayout.openDrawer(sideNavigationView) })
  }

  override fun onUserHistoricalEntriesClicked(): Observable<Any> {
    logger.debug("onUserHistoricalEntriesClicked")
    return RxView.clicks(buttonHistoricalEntries)
  }

  //todo: really wanted to make this return Completable, but rx noob herp derp
  override fun retrieveHardwareBackCamera(): Observable<Any> {
    logger.debug("retrieveHardwareBackCamera")
    return Observable.create { emitter ->
      if (cameraInstance == null) {
        cameraInstance = openHardwareBackCamera()
        cameraInstance?.setErrorCallback(this::onCameraError)
        logger.debug("retrieveHardwareBackCamera, retrieved camera instance: $cameraInstance")
      } else {
        logger.debug("retrieveHardwareBackCamera, cached camera instance: $cameraInstance")
      }
      cameraInstance?.let {
        emitter.onNext(cameraInstance)
      } ?:
          emitter.onError(ERROR_CAMERA_RETRIEVAL)
    }
  }

  override fun initializeCameraPreviewRendering(): Observable<CameraSurfaceEvent> {
    logger.debug("initializeCameraPreviewRendering")
    cameraSurfaceView.initialize()
    return cameraSurfaceView.subscribeForEvents()
  }

  override fun configureAndStartRealtimeCameraRendering() {
    logger.debug("configureAndStartRealtimeCameraRendering, camera: $cameraInstance")
    cameraInstance?.let {
      it.configureCameraDisplayOrientation(this.windowManager.defaultDisplay)
      it.configureFocusMode()
      it.setPreviewDisplay(cameraSurfaceView.holder)
      it.startPreview()
    }
  }

  override fun stopRenderingRealtimeCameraPreview() {
    logger.debug("stopRenderingRealtimeCameraPreview")
  }

  override fun renderLoadingAnimation() {
    logger.debug("renderLoadingAnimation")
    floatingWordsView.renderLoadingHalo()
  }

  override fun stopRenderingLoadingAnimation() {
    logger.debug("stopRenderingLoadingAnimation")
    floatingWordsView.stopRenderingLoadingHalo()
  }

  override fun renderNonTranslatedWords(nonTranslatedWords: List<String>) {
    logger.debug("renderNonTranslatedWords, words: $nonTranslatedWords")
    floatingWordsView.renderAuxiliaryWords(nonTranslatedWords)
  }

  override fun renderTranslatedWords(translatedWords: List<String>) {
    logger.debug("renderNonTranslatedWords, words: $translatedWords")
    floatingWordsView.renderPrimaryWords(translatedWords)
  }

  override fun stopRenderingWords() {
    logger.debug("stopRenderingWords")
    floatingWordsView.clearWords()
  }

  private fun openHardwareBackCamera(): Camera? {
    logger.debug("openHardwareBackCamera")
    try {
      return Camera.open()
    } catch (exc: RuntimeException) {
      exc.printStackTrace()
      //todo: ...logging, show error, whatever
    }
    return null
  }

  override fun takePicture(): Observable<CameraPictureEvent> {
    logger.debug("takePicture")
    return Observable.create({ emitter ->
      cameraInstance?.takePicture(
          Camera.ShutterCallback { emitter.onNext(CameraPictureEvent(CAMERA_SHUTTER_EVENT)) },
          null,
          Camera.PictureCallback { data, _ ->
            cameraInstance?.startPreview()
            emitter.onNext(CameraPictureEvent(CAMERA_BYTES_RETRIEVED, data))
          }
      )
    })
  }

  override fun scaleCompressEncodePictureByteArray(pictureByteArray: ByteArray)
      : Observable<CameraPictureEvent> {
    logger.debug("scaleCompressEncodePictureByteArray, picture: ${pictureByteArray.hashCode()}")
    return Observable.create { emitter ->
      try {
        val options = BitmapFactory.Options().apply {
          this.inPurgeable = true
          rescaleImageRequestFactor(8,
              this) //todo: refactor so that i can specify minimum res (600-720px) instead of scaling //todo: because i do not know how powerful user camera is
        }
        val scaledBitmap = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size,
            options)
        val compressedByteStream = ByteArrayOutputStream()
        scaledBitmap.compress(JPEG, 60, compressedByteStream)
        if (compressedByteStream.size() <= 0) emitter.onError(ERROR_COMPRESSED_BYTES_INVALID_SIZE)
        else emitter.onNext(CameraPictureEvent(compressedByteStream.toByteArray()))
      } catch (exc: Throwable) {
        emitter.onError(exc)
      }
    }
  }

  private fun rescaleImageRequestFactor(
      downScaleFactor: Int,
      bitmapOptions: BitmapFactory.Options): BitmapFactory.Options {
    logger.debug("rescaleImageRequestFactor, downScaleFactor: $downScaleFactor, options: $bitmapOptions")
    bitmapOptions.inSampleSize = downScaleFactor
    return bitmapOptions
  }

  override fun initializeTextToSpeechClient(locale: Locale) {
    logger.debug("initializeTextToSpeechClient, locale: $locale")
    textToSpeechClient = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
      status ->
      status.takeIf { code -> code != TextToSpeech.ERROR }?.run {
        textToSpeechClient.language = locale
      }
    })
  }

  override fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean) {
    logger.debug("speakText, textToSpeak: $textToSpeak, flushSpeakingQueue: $flushSpeakingQueue")
    textToSpeechClient.speak(textToSpeak,
        if (flushSpeakingQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null)
  }

  override fun initializeSuddenMovementDetection() {
    logger.debug("initializeSuddenMovementDetection")
  }

  override fun onSuddenMovementDetected() {
    logger.debug("onSuddenMovementDetected")
  }


  override fun renderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("renderUserAlertMessage.alert: $userAlertMessage")
    when (userAlertMessage) {
      UserAlertMessage.CAMERA_PERMISSION_ALERT -> {
        fullscreenMessageInfo.show(R.drawable.ic_action_camera_dark,
            R.string.dialog_alert_permission_camera)
        fullscreenMessageType = UserAlertMessage.CAMERA_PERMISSION_ALERT
      }
      UserAlertMessage.STORAGE_PERMISSION_ALERT -> {
        fullscreenMessageInfo.show(R.drawable.ic_action_camera_dark,
            R.string.dialog_alert_permission_storage)
        fullscreenMessageType = UserAlertMessage.STORAGE_PERMISSION_ALERT
      }
    }
  }

  override fun stopRenderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("renderUserAlertMessage.alert: $userAlertMessage")
    if (fullscreenMessageType == userAlertMessage)
      fullscreenMessageInfo.hide()
  }

  private fun onCameraError(errorCode: Int, cameraInstance: Camera) {
    var errorString =
        when (errorCode) {
          Camera.CAMERA_ERROR_EVICTED -> "CAMERA_ERROR_EVICTED"
          Camera.CAMERA_ERROR_SERVER_DIED -> "CAMERA_ERROR_SERVER_DIED"
          else -> "CAMERA_ERROR_UNKNOWN"
        }
    logger.error("onCameraError, errorCode: $errorCode, errorString: $errorString, camera: $cameraInstance", ERROR_CAMERA_NATIVE_EXCEPTION) //todo: exception
  }


}
