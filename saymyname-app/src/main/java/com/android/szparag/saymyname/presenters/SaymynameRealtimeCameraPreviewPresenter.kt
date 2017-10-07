package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_RETRIEVED
import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.CAMERA_PERMISSION
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.STORAGE_ACCESS
import com.android.szparag.saymyname.utils.computation
import com.android.szparag.saymyname.utils.isNotGranted
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.activities.HistoricalEntriesActivity
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.contracts.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.Locale


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
    logger.debug("onAttached")
    subscribeViewPermissionsEvents()
    subscribeModelEvents()
  }

  override fun onViewReady() {
    logger.debug("onViewReady")
    initializeCameraPreviewView()
  }

  override fun onBeforeDetached() {
    logger.debug("onBeforeDetached")
    super.onBeforeDetached()
    view?.stopRenderingLoadingAnimation()
    view?.stopRenderingRealtimeCameraPreview()
  }


  fun subscribeModelEvents() {
    logger.debug("subscribeModelEvents")
    model
        .attach()
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onComplete = {
              logger.debug("subscribeModelEvents.model.attach.onComplete()")
              subscribeViewUserEvents()
              subscribeNewWords()
              initializeTextToSpeechClient(Locale.UK)
            },
            onError = { exc ->
              logger.error("subscribeModelEvents.model.attach.onError()", exc)
            })
        .toModelDisposable()
  }

  fun subscribeViewPermissionsEvents() {
    logger.debug("subscribeViewPermissionsEvents")
    view
        ?.subscribeForPermissionsChange()
        ?.doOnSubscribe { view?.checkPermissions(CAMERA_PERMISSION, STORAGE_ACCESS) }
        ?.ui()
        ?.subscribeBy(
            onNext = { permissionEvent ->
              logger.debug(
                  "subscribeViewPermissionsEvents.view?.subscribeForPermissionsChange.onNext, ev: $permissionEvent")
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
                Presenter.PermissionType.STORAGE_ACCESS    -> {
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
            onComplete = {
              logger.debug(
                  "subscribeViewPermissionsEvents.view?.subscribeForPermissionsChange.onComplete")
            },
            onError = { exc ->
              logger.error(
                  "subscribeViewPermissionsEvents.view?.subscribeForPermissionsChange.onError", exc)
            })
        .toViewDisposable()
  }

  @Suppress("NON_EXHAUSTIVE_WHEN")
  fun subscribeViewUserEvents() {
    logger.debug("subscribeViewUserEvents")
    view
        ?.onUserTakePictureButtonClicked()
        ?.ui()
        ?.doOnNext {
          view?.renderLoadingAnimation()
          view?.bottomSheetUnpeek()
        }
        ?.flatMap {
          view?.takePicture()?.ui()
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
          model.requestImageProcessingWithTranslation(currentImageRecognitionModel,
              pictureEvent.cameraImageBytes, nativeLanguageCode, currentForeignLanguageString)
        }
        ?.doOnEach {
          view?.stopRenderingLoadingAnimation()
        }
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribeBy(
            onNext = { image ->
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserTakePictureButtonClicked.onNext, image: ${image.hashCode()}")
            },
            onComplete = {
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserTakePictureButtonClicked.onComplete")
            },
            onError = { exc ->
              logger.error(
                  "subscribeViewUserEvents.view?.onUserTakePictureButtonClicked.onError", exc)
              exc.printStackTrace()
            })
        .toViewDisposable()

    view
        ?.onUserModelSwitchButtonClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserMODELSwitchButtonClicked.onNext, selected: $it")
              currentImageRecognitionModel = it
            },
            onComplete = {
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserMODELSwitchButtonClicked.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeViewUserEvents.view?.onUserMODELSwitchButtonClicked.onError",
                  exc)
              exc.printStackTrace()
            })
        .toViewDisposable()

    view
        ?.onUserLanguageSwitchClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserLANGUAGESwitchClicked.onNext, selected: $it")
              currentForeignLanguageString = it
            },
            onComplete = {
              logger.debug("subscribeViewUserEvents.view?.onUserLANGUAGESwitchClicked.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeViewUserEvents.view?.onUserLANGUAGESwitchClicked.onError", exc)
              exc.printStackTrace()
            })
        .toViewDisposable()

    view
        ?.onUserHistoricalEntriesClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = {
              logger.debug("subscribeViewUserEvents.view?.onUserHistoricalEntriesClicked.onNext")
              view?.startActivity(HistoricalEntriesActivity::class.java)
            },
            onComplete = {
              logger.debug(
                  "subscribeViewUserEvents.view?.onUserHistoricalEntriesClicked.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeViewUserEvents.view?.onUserHistoricalEntriesClicked.onError",
                  exc)
              exc.printStackTrace()
            })
        .toViewDisposable()

    view
        ?.onUserHamburgerMenuClicked()
        ?.ui()
        ?.subscribe()
        ?.toViewDisposable()
  }

  override fun subscribeNewWords() {
    logger.debug("subscribeNewWords")
    model
        .observeNewWords()
        .ui()
        .subscribeBy(
            onNext = { image ->
              logger.debug("subscribeNewWords.model.observeNewWords.onNext, image: $image")
              view?.renderNonTranslatedWords(image.getNonTranslatedWords())
              view?.renderTranslatedWords(image.getTranslatedWords())
              view?.bottomSheetPeek()
              image.imageBase64?.let {
                view?.bottomSheetFillData(
                    it, image.getNonTranslatedWords(), image.getTranslatedWords(), image.dateTime
                )
              }
            },
            onComplete = {
              logger.debug("subscribeNewWords.model.observeNewWords.onNext.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeNewWords.model.observeNewWords.onError", exc)
              exc.printStackTrace()
            })
        .toModelDisposable()
  }


  //todo: permissions stuff
  //todo: if perm isnt granted - dont run this code, if it is - do run it.
  //todo: immediately after granting permission - run this code.
  override fun initializeCameraPreviewView() {
    logger.debug("initializeCameraPreviewView")
    view
        ?.retrieveHardwareBackCamera()
        ?.ui()
        ?.flatMap { view?.initializeCameraPreviewRendering()?.ui() }
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribeBy(
            onNext = { event ->
              logger.debug("initializeCameraPreviewView.view?.retrieveHardwareBackCamera.onNext, ev: $event")
              view?.configureAndStartRealtimeCameraRendering()
            },
            onComplete = {
              logger.debug("initializeCameraPreviewView.view?.retrieveHardwareBackCamera.onComplete")
            },
            onError = { exc ->
              logger.error("initializeCameraPreviewView.view?.retrieveHardwareBackCamera.onError", exc)
              exc.printStackTrace()
            }
        )
        .toViewDisposable()

  }

  override fun onCameraSetupFailed(exc: Throwable) {
    logger.error("onCameraSetupFailed", exc)
    exc.printStackTrace()
  }

  override fun initializeTextToSpeechClient(locale: Locale) {
    logger.debug("initializeTextToSpeechClient, locale: $locale")
    view?.initializeTextToSpeechClient(locale)
  }

  override fun startCameraRealtimePreview() = logger.debug("startCameraRealtimePreview")
}