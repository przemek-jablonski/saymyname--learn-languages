package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.utils.add
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.logMethodError
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.Locale

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class SaymynameRealtimeCameraPreviewPresenter(
    override val model: RealtimeCameraPreviewModel
) : BasePresenter(), RealtimeCameraPreviewPresenter {

  val viewSubscription: CompositeDisposable = CompositeDisposable()
  val modelSubscription: Disposable? = null

  //todo: this is fucked up
  fun getView(): RealtimeCameraPreviewView? {
    return this.view as RealtimeCameraPreviewView? //todo: why view can be nullable?
  }

  override fun onAttached() {
    super.onAttached()
    model.attach()
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onComplete = {
              logMethodError("ONATTACHED.onComplete()")
              observeView()
              observeNewWords()
              initializeTextToSpeechClient(Locale.UK)
            },
            onError= { logMethod("ONATTACHED.onError(), $it") })
  }

  override fun onBeforeDetached() {
    logMethod()
    super.onBeforeDetached()
    viewSubscription.clear()
    modelSubscription?.dispose()
    getView()?.stopRenderingLoadingAnimation()
    getView()?.stopRenderingRealtimeCameraPreview()
  }


  @Suppress("NON_EXHAUSTIVE_WHEN")
  private fun observeView() {
    viewSubscription.add(
        getView()?.onUserTakePictureButtonClicked()
            ?.ui()
            ?.flatMap { getView()?.takePicture()?.ui() }
            ?.doOnNext { this::processCameraPictureEvents }
            ?.filter { it.type == CAMERA_BYTES_RETRIEVED }
            ?.flatMap { it.cameraImageBytes?.let { bytes ->
                getView()?.scaleCompressEncodePictureByteArray(bytes)
              }?.subscribeOn(Schedulers.computation())
            }
            ?.flatMapCompletable { pictureEvent ->
              model.requestImageProcessingWithTranslation("aaa03c23b3724a16a56b629203edc62c",
                  pictureEvent.cameraImageBytes, -1, -1, "en-it")
            }
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onComplete = { logMethod("OBSERVEVIEW.onUserTakePictureButtonClicked: onComplete") },
                onError = { logMethodError("OBSERVEVIEW.onUserTakePictureButtonClicked: onError, throwable: ($it)") }
            )
    )

    viewSubscription.add(
        getView()
            ?.onUserModelSwitchButtonClicked()
            ?.ui()
            ?.subscribeBy(
                onComplete = { logMethod("OBSERVEVIEW.onUserModelSwitchButtonClicked: onComplete") },
                onError = { logMethodError("OBSERVEVIEW.onUserModelSwitchButtonClicked: onError, throwable: ($it)") }
            )
    )

    viewSubscription.add(
        getView()
            ?.onUserModelSwitchLanguageClicked()
            ?.ui()
            ?.subscribeBy(
                onComplete = { logMethod("OBSERVEVIEW.onUserModelSwitchLanguageClicked: onComplete") },
                onError = { logMethodError("OBSERVEVIEW.onUserModelSwitchLanguageClicked: onError, throwable: ($it)") }
            )
    )
  }

  fun processCameraPictureEvents(cameraPictureEvent: CameraPictureEvent) {
    //todo: trigger UI changes based on type of cameraPictureEvent
  }


  override fun observeNewWords() {
    model.observeNewWords()
        .ui()
        .subscribeBy(
            onNext = { image ->
              logMethod("OBSERVENEWWORDS.onNext, image: $image")
              getView()?.renderNonTranslatedWords(image.getNonTranslatedWords())
              getView()?.renderTranslatedWords(image.getTranslatedWords())
            },
            onError = { logMethodError("OBSERVENEWWORDS.onError, throwable: $it") },
            onComplete = { logMethod("OBSERVENEWWORDS.onComplete ") }
        )
  }

  override fun onViewReady() {
    logMethod()
    initializeCameraPreviewView()
  }

  override fun initializeCameraPreviewView() {
    logMethod()
    getView()?.let {
      it.initializeCameraPreviewSurfaceView()
          .ui()
          .andThen(it.retrieveHardwareBackCamera().ui())
          .andThen(it.renderRealtimeCameraPreview().ui())
          .subscribeBy (
              onComplete = { logMethod("initializeCameraPreviewView().onComplete") },
              onError = { this::onCameraSetupFailed }
          )
    }
  }

  override fun onCameraSetupFailed(exc: Throwable) {
    logMethodError("onCameraSetupFailed, $exc")
  }

  override fun initializeTextToSpeechClient(locale: Locale) {
    logMethod()
    getView()?.initializeTextToSpeechClient(locale)
  }

  override fun startCameraRealtimePreview() {
    logMethod()
  }
}