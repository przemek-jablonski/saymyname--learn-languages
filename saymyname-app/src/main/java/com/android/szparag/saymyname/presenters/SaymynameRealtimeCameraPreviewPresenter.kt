package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.CAMERA_PERMISSION
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.STORAGE_ACCESS
import com.android.szparag.saymyname.utils.isNotGranted
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.logMethodError
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.activities.HistoricalEntriesActivity
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.contracts.View
import java.util.Locale
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class SaymynameRealtimeCameraPreviewPresenter(
    override val model: RealtimeCameraPreviewModel
) : BasePresenter<RealtimeCameraPreviewView>(), RealtimeCameraPreviewPresenter {

  override fun onAttached() {
    super.onAttached()
    subscribeViewPermissionsEvents()
    view?.checkPermissions(CAMERA_PERMISSION, STORAGE_ACCESS)
    subscribeModelEvents()
  }

  fun subscribeModelEvents() {
    model.attach()
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onComplete = {
              logMethodError("ONATTACHED.onComplete()")
              subscribeViewUserEvents()
              subscribeNewWords()
              initializeTextToSpeechClient(Locale.UK)
            },
            onError = { logMethod("ONATTACHED.onError(), $it") })
        .toModelDisposable()
  }

  override fun onBeforeDetached() {
    logMethod()
    super.onBeforeDetached()
    view?.stopRenderingLoadingAnimation()
    view?.stopRenderingRealtimeCameraPreview()
  }

  fun subscribeViewPermissionsEvents() {
    view?.subscribeForPermissionsChange()?.ui()?.subscribeBy(
        onNext = { permissionEvent ->
          logMethod("subscribeViewPermissionsEvents.onNext, ev: $permissionEvent")
          when (permissionEvent.permissionType) {
            Presenter.PermissionType.CAMERA_PERMISSION -> {
              if (permissionEvent.permissionResponse.isNotGranted()) {
                view?.renderUserAlertMessage(View.UserAlertMessage.CAMERA_PERMISSION_ALERT)
              }
            }
            Presenter.PermissionType.STORAGE_ACCESS -> { }
          }
        },
        onError = {
          logMethod("subscribeViewPermissionsEvents.onError, exc: $it")
        },
        onComplete = {
          logMethod("subscribeViewPermissionsEvents.onComplete")
        }
    ).toViewDisposable()
  }

  @Suppress("NON_EXHAUSTIVE_WHEN")
  fun subscribeViewUserEvents() {
    view?.onUserTakePictureButtonClicked()
        ?.ui()
        ?.flatMap { view?.takePicture()?.ui() }
        ?.doOnNext { this::processCameraPictureEvents }
        ?.filter { it.type == CAMERA_BYTES_RETRIEVED }
        ?.flatMap {
          it.cameraImageBytes?.let { bytes ->
            view?.scaleCompressEncodePictureByteArray(bytes)
          }?.subscribeOn(Schedulers.computation())
        }
        ?.flatMapCompletable { pictureEvent ->
          model.requestImageProcessingWithTranslation("aaa03c23b3724a16a56b629203edc62c",
              pictureEvent.cameraImageBytes, -1, -1, "en-it")
        }
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribeBy(
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: onError, throwable: ($it)")
            })
        .toViewDisposable()

    view?.onUserModelSwitchButtonClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchLanguageClicked: onNext")
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchButtonClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchButtonClicked: onError, throwable: ($it)")
            })
        .toViewDisposable()

    view?.onUserModelSwitchLanguageClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchLanguageClicked: onNext")
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchLanguageClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchLanguageClicked: onError, throwable: ($it)")
            })
        .toViewDisposable()

    view?.onUserHistoricalEntriesClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserModelSwitchLanguageClicked: onNext")
              view?.startActivity(HistoricalEntriesActivity::class.java)
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserHistoricalEntriesClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserHistoricalEntriesClicked: onError, throwable: ($it)")
            })
        .toViewDisposable()
  }

  fun processCameraPictureEvents(cameraPictureEvent: CameraPictureEvent) {
    //todo: trigger UI changes based on type of cameraPictureEvent
  }

  override fun subscribeNewWords() {
    model.observeNewWords()
        .ui()
        .subscribeBy(
            onNext = { image ->
              logMethod("OBSERVENEWWORDS.onNext, image: $image")
              view?.renderNonTranslatedWords(image.getNonTranslatedWords())
              view?.renderTranslatedWords(image.getTranslatedWords())
            },
            onError = { logMethodError("OBSERVENEWWORDS.onError, throwable: $it") },
            onComplete = { logMethod("OBSERVENEWWORDS.onComplete ") })
        .toModelDisposable()
  }

  override fun onViewReady() {
    logMethod()
    initializeCameraPreviewView()
  }

  override fun initializeCameraPreviewView() {
    logMethod()
    view?.let { view ->
      view.initializeCameraPreviewSurfaceView()
          .ui()
          .andThen(view.retrieveHardwareBackCamera().ui())
          .andThen(view.renderRealtimeCameraPreview().ui())
          .subscribeBy(
              onComplete = { logMethod("initializeCameraPreviewView().onComplete") },
              onError = { this::onCameraSetupFailed })
          .toViewDisposable()
    }
  }

  override fun onCameraSetupFailed(exc: Throwable) {
    logMethodError("onCameraSetupFailed, $exc")
  }

  override fun initializeTextToSpeechClient(locale: Locale) {
    logMethod()
    view?.initializeTextToSpeechClient(locale)
  }

  override fun startCameraRealtimePreview() {
    logMethod()
  }
}