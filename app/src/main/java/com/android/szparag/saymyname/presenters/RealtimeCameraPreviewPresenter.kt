package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.presenters.contracts.CameraPresenter
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.presenters.contracts.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Model
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class RealtimeCameraPreviewPresenter : BasePresenter<RealtimeCameraPreviewView>(), CameraPresenter, ImageProcessingPresenter {

  override fun onViewReady() {
    initializeCameraPreviewView()
  }

  override fun onBeforeDetached() {
    super.onBeforeDetached()
    view?.stopRenderingLoadingAnimation()
    view?.stopRenderingRealtimeCameraPreview()
  }

  override fun initializeCameraPreviewView() {
    view?.let {
      it.initializeCameraPreviewSurfaceView()
      it.retrieveHardwareBackCamera()
      it.renderRealtimeCameraPreview()
    }
  }

  override fun onCameraPreviewViewInitialized() {}

  override fun onCameraPreviewViewInitializationFailed() {}

  override fun initializeTextToSpeechClient() {
    view?.initializeTextToSpeechClient()
  }

  override fun startCameraRealtimePreview() {//...
  }

  override fun onUserTakePictureButtonClicked() {
    takeCameraPicture()
  }

  override fun takeCameraPicture() {
    view?.takePicture()
  }

  override fun onCameraPhotoTaken() {
    //todo: fire up more dense Google'y Vision animations
    //todo: fire up one-shot that symbolizes camera flash on shutter click
    //todo: fire up loading bar at the bottom
    //todo: hide bottom sheet if exists (bottom loading bar goes there)
  }

  override fun onCameraPhotoByteArrayReady(photoByteArray: ByteArray) {
    //todo: make user feel that he is (almost) half of the way in image processing
    //todo: (take photo -> process -> send to clarifai -> send to translator)
    //todo: change loading bar colour maybe
    requestImageVisionData()
  }


  override fun requestImageVisionData() {//...
  }

  override fun onImageVisionDataReceived(visionConcepts: List<Concept>, model: Model) {//...
  }

  override fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus) {
    //todo: make loading bar slowly stop and before that, change colour to red
  }

  override fun requestTranslation() {//...
  }

  override fun onTranslationDataReceived(translatedText: List<String>) {//...
  }

  override fun onTranslationDataFailed(requestStatus: NetworkRequestStatus) {
    //todo: make loading bar slowly stop and before that, change colour to red
  }


}