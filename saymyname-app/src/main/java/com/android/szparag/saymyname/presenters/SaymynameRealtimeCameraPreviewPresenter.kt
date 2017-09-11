package com.android.szparag.saymyname.presenters

import android.graphics.Camera
import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.events.CameraSurfaceEvent
import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.CAMERA_PERMISSION
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.STORAGE_ACCESS
import com.android.szparag.saymyname.utils.computation
import com.android.szparag.saymyname.utils.isNotGranted
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.logMethodError
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.activities.HistoricalEntriesActivity
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.contracts.View
import io.reactivex.Observable
import io.reactivex.ObservableSource
import java.util.Locale
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class SaymynameRealtimeCameraPreviewPresenter(
    override val model: RealtimeCameraPreviewModel
) : BasePresenter<RealtimeCameraPreviewView>(), RealtimeCameraPreviewPresenter {

  private lateinit var currentImageRecognitionModel: String
  private var nativeLanguageCode: String = "en"
  private lateinit var currentForeignLanguageString: String

  //primary presenter lifecycle:
  override fun onAttached() {
    super.onAttached()
    subscribeViewPermissionsEvents()
    subscribeModelEvents()
  }

  override fun onViewReady() {
    logMethod()
    initializeCameraPreviewView()
  }

  override fun onBeforeDetached() {
    logMethod()
    super.onBeforeDetached()
    view?.stopRenderingLoadingAnimation()
    view?.stopRenderingRealtimeCameraPreview()
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
            onError = { logMethod("ONATTACHED.onError(), $it")
              it.printStackTrace() })
        .toModelDisposable()
  }

  fun subscribeViewPermissionsEvents() {
    view?.subscribeForPermissionsChange()
        ?.doOnSubscribe {
          logMethod("subscribeViewPermissionsEvents.sub")
          view?.checkPermissions(CAMERA_PERMISSION, STORAGE_ACCESS)
        }
        ?.ui()
        ?.subscribeBy(
            onNext = { permissionEvent ->
              logMethod("subscribeViewPermissionsEvents.onNext, ev: $permissionEvent")
              when (permissionEvent.permissionType) {
                Presenter.PermissionType.CAMERA_PERMISSION -> {
                  if (permissionEvent.permissionResponse.isNotGranted()) {
                    view?.renderUserAlertMessage(View.UserAlertMessage.CAMERA_PERMISSION_ALERT)
                    if (Presenter.PermissionType.CAMERA_PERMISSION.permissionAskCount == 0) view?.requestPermissions(
                        Presenter.PermissionType.CAMERA_PERMISSION)
                  } else {
                    view?.stopRenderUserAlertMessage(View.UserAlertMessage.CAMERA_PERMISSION_ALERT)
                  }
                }
                Presenter.PermissionType.STORAGE_ACCESS -> {
                  if (permissionEvent.permissionResponse.isNotGranted()) {
                    view?.renderUserAlertMessage(View.UserAlertMessage.STORAGE_PERMISSION_ALERT)
                    if (Presenter.PermissionType.STORAGE_ACCESS.permissionAskCount == 0) view?.requestPermissions(
                        Presenter.PermissionType.STORAGE_ACCESS)
                  } else {
                    view?.stopRenderUserAlertMessage(View.UserAlertMessage.STORAGE_PERMISSION_ALERT)
                  }
                }
              }
            },
            onError = { logMethod("subscribeViewPermissionsEvents.onError, exc: $it") },
            onComplete = { logMethod("subscribeViewPermissionsEvents.onComplete") })
        .toViewDisposable()
  }

  @Suppress("NON_EXHAUSTIVE_WHEN")
  fun subscribeViewUserEvents() {
    view?.onUserTakePictureButtonClicked()
        ?.ui()
        ?.doOnNext {
          view?.renderLoadingAnimation()
          view?.bottomSheetUnpeek()
        }
        ?.flatMap {
          view?.takePicture()?.ui()
        }
        ?.doOnNext {
          this::processCameraPictureEvents
        }
        ?.filter {
          it.type == CAMERA_BYTES_RETRIEVED
        }
        ?.flatMap {
          it.cameraImageBytes
              ?.let { bytes -> view?.scaleCompressEncodePictureByteArray(bytes) }
              ?.computation()
        }
        ?.flatMap { pictureEvent ->
          model.requestImageProcessingWithTranslation(currentImageRecognitionModel, pictureEvent.cameraImageBytes, nativeLanguageCode, currentForeignLanguageString)
        }
        ?.doOnEach {
          logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: doOnEach")
          view?.stopRenderingLoadingAnimation()
        }
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: onNext")
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: onComplete")
            },
            onError = {
              logMethodError("SUBSCRIBEVIEWUSEREVENTS.onUserTakePictureButtonClicked: onError, throwable: ($it)")
              it.printStackTrace()
            })
        .toViewDisposable()

    view?.onUserModelSwitchButtonClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserMODELSwitchButtonClicked: onNext, selected: $it")
              currentImageRecognitionModel = it
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserMODELSwitchButtonClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserMODELSwitchButtonClicked: onError, throwable: ($it)")
              it.printStackTrace()
            })
        .toViewDisposable()

    view?.onUserLanguageSwitchClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserLANGUAGESwitchClicked: onNext, selected: $it")
              currentForeignLanguageString = it
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserLANGUAGESwitchClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserLANGUAGESwitchClicked: onError, throwable: ($it)")
              it.printStackTrace()
            })
        .toViewDisposable()

    view?.onUserHistoricalEntriesClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserLanguageSwitchClicked: onNext")
              view?.startActivity(HistoricalEntriesActivity::class.java)
            },
            onComplete = {
              logMethod("SUBSCRIBEVIEWUSEREVENTS.onUserHistoricalEntriesClicked: onComplete")
            },
            onError = {
              logMethodError(
                  "SUBSCRIBEVIEWUSEREVENTS.onUserHistoricalEntriesClicked: onError, throwable: ($it)")
              it.printStackTrace()
            })
        .toViewDisposable()

    view?.onUserHamburgerMenuClicked()
        ?.ui()
        ?.subscribe()
        ?.toViewDisposable()
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
              view?.bottomSheetPeek()
              image.imageBase64?.let {
                view?.bottomSheetFillData(
                    it, image.getNonTranslatedWords(), image.getTranslatedWords(), image.dateTime
                )
              }
            },
            onError = { logMethodError("OBSERVENEWWORDS.onError, throwable: $it")
              it.printStackTrace() },
            onComplete = { logMethod("OBSERVENEWWORDS.onComplete ") })
        .toModelDisposable()
  }


  //todo: permissions stuff
  //todo: if perm isnt granted - dont run this code, if it is - do run it.
  //todo: immediately after granting permission - run this code.
  override fun initializeCameraPreviewView() {
    logMethod()
    view?.initializeCameraPreviewRendering()
        ?.ui()
        ?.startWith { view?.retrieveHardwareBackCamera()?.ui() }
//    view?.retrieveHardwareBackCamera()
//        ?.ui()
////        ?.
////        ?.toObservable<CameraSurfaceEvent>()
//        ?.startWith(){ view?.initializeCameraPreviewRendering() }
        ?.subscribeBy (
            onNext = { event ->
              logMethod("initializeCameraPreviewView.onNext, ev: $event")
              view?.configureAndStartRealtimeCameraRendering()
            },
            onError = { exc ->
              logMethodError("initializeCameraPreviewView.ONERROR, exc: $exc")
            },
            onComplete = {
              logMethodError("initializeCameraPreviewView.onComplete")
            }
        )
        .toViewDisposable()

  }

  override fun onCameraSetupFailed(exc: Throwable) {
    logMethodError("onCameraSetupFailed, $exc")
    exc.printStackTrace()
  }

  override fun initializeTextToSpeechClient(locale: Locale) {
    logMethod()
    view?.initializeTextToSpeechClient(locale)
  }

  override fun startCameraRealtimePreview() {
    logMethod()
  }
}