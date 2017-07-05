package com.android.szparag.saymyname.presenters

import android.os.Handler
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.presenters.contracts.ImageProcessingPresenter
import com.android.szparag.saymyname.presenters.contracts.RealtimeCameraPresenter
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Model
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class RealtimeCameraPreviewPresenter : BasePresenter<RealtimeCameraPreviewView>(), RealtimeCameraPresenter {



  override fun onViewReady() {
    logMethod()
//    Handler().postDelayed(
        initializeCameraPreviewView()
//        1000)
  }

  override fun onBeforeDetached() {
    logMethod()
    super.onBeforeDetached()
    view?.stopRenderingLoadingAnimation()
    view?.stopRenderingRealtimeCameraPreview()
  }

  override fun initializeCameraPreviewView() {
    logMethod()
    view?.let {
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
    view?.initializeTextToSpeechClient()
  }

  override fun startCameraRealtimePreview() {
    logMethod()
  }

  override fun onUserTakePictureButtonClicked() {
    logMethod()
    takeCameraPicture()
  }

  override fun takeCameraPicture() {
    logMethod()
    view?.takePicture()
  }

  override fun onCameraPhotoTaken() {
    logMethod()
    //todo: fire up more dense Google'y Vision animations
    //todo: fire up one-shot that symbolizes camera flash on shutter click
    //todo: fire up loading bar at the bottom
    //todo: hide bottom sheet if exists (bottom loading bar goes there)
  }

  override fun onCameraPhotoByteArrayReady(photoByteArray: ByteArray) {
    logMethod()
    //todo: make user feel that he is (almost) half of the way in image processing
    //todo: (take photo -> process -> send to clarifai -> send to translator)
    //todo: change loading bar colour maybe
    requestImageVisionData()
  }


  override fun requestImageVisionData() {
    logMethod()
  }

  override fun onImageVisionDataReceived(visionConcepts: List<Concept>, model: Model) {
    logMethod()
  }

  override fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }

  override fun requestTranslation() {
    logMethod()
  }

  override fun onTranslationDataReceived(translatedText: List<String>) {
    logMethod()
  }

  override fun onTranslationDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }


}