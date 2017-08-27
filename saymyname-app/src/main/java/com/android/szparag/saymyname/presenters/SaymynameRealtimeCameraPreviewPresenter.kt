package com.android.szparag.saymyname.presenters

import android.util.Log
import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.utils.add
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

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
        .subscribe({
          logMethod("ONATTACHED.onComplete()")
          observeView()
          observeNewWords()
          initializeTextToSpeechClient()
        }, {
          logMethod("ONATTACHED.onError()")
        })
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
            ?.subscribeOn(AndroidSchedulers.mainThread())
            ?.flatMap { getView()?.takePicture()?.subscribeOn(AndroidSchedulers.mainThread()) }
            ?.doOnNext { this::processCameraPictureEvents }
            ?.filter { it.type == CAMERA_BYTES_RETRIEVED }
            ?.flatMap {
              it.cameraImageBytes?.let { bytes ->
                getView()?.scaleCompressEncodePictureByteArray(bytes)
              }?.subscribeOn(Schedulers.computation())
            }
            ?.flatMapCompletable { pictureEvent ->
              model.requestImageProcessingWithTranslation("aaa03c23b3724a16a56b629203edc62c",
                  pictureEvent.cameraImageBytes, -1, -1, "en-it")
            }
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeBy(
                onComplete = { logMethod("OBSERVEVIEW: onComplete") },
                onError = { logMethod("OBSERVEVIEW: onError, throwable: ($it)") }
            )
    )
  }

  fun processCameraPictureEvents(cameraPictureEvent: CameraPictureEvent) {
    //todo: process UI changes based on type of cameraPictureEvent
  }


  override fun observeNewWords() {
    model.observeNewWords()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ image ->
          logMethod("OBSERVENEWWORDS.onNext, image: $image")
          getView()?.renderNonTranslatedWords(image.getNonTranslatedWords())
          getView()?.renderTranslatedWords(image.getTranslatedWords())
        }, {
          logMethod("OBSERVENEWWORDS.onError, throwable: $it")
        }, {
          logMethod("OBSERVENEWWORDS.onComplete ")
        }
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
      it.retrieveHardwareBackCamera()
      it.renderRealtimeCameraPreview()
    }
  }

  override fun onCameraPreviewViewInitialized() {
    logMethod()
  }

  override fun onCameraPreviewViewInitializationFailed() {
    logMethod()
  }

  override fun initializeTextToSpeechClient() {
    logMethod()
    getView()?.initializeTextToSpeechClient()
  }

  override fun startCameraRealtimePreview() {
    logMethod()
  }

  override fun requestImageVisionData(imageByteArray: ByteArray) {
    logMethod()

  }

  override fun requestTranslation(textsToTranslate: List<String>) {
    logMethod()
  }

}