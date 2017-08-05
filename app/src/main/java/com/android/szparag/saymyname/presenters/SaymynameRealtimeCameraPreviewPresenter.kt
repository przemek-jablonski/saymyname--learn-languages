package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.presenters.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.subListSafe
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class SaymynameRealtimeCameraPreviewPresenter(
    override val model: RealtimeCameraPreviewModel
) : BasePresenter(), RealtimeCameraPreviewPresenter {


  //todo: this is fucked up
  fun getView(): RealtimeCameraPreviewView? {
    return this.view as RealtimeCameraPreviewView?
  }

  override fun onAttached() {
    super.onAttached()
    model.attach(this)
    initializeTextToSpeechClient()
  }

  override fun onViewReady() {
    logMethod()
    initializeCameraPreviewView()
  }

  override fun onBeforeDetached() {
    logMethod()
    super.onBeforeDetached()
    getView()?.stopRenderingLoadingAnimation()
    getView()?.stopRenderingRealtimeCameraPreview()
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

  override fun onUserTakePictureButtonClicked() {
    logMethod()
    getView()?.stopRenderingWords()
    takeCameraPicture()
  }

  override fun takeCameraPicture() {
    logMethod()
    getView()?.takePicture()
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
    getView()?.scaleCompressEncodePictureByteArray(photoByteArray)
  }

  override fun onCameraCompressedPhotoByteArrayReady(compressedPhotoByteArray: ByteArray) {
    requestImageVisionData(compressedPhotoByteArray)
  }


  override fun requestImageVisionData(photoByteArray: ByteArray) {
    logMethod()
    //todo: from presenter i can access presenter, service and repository, THATS BAD
    model.requestImageProcessing(
        "aaa03c23b3724a16a56b629203edc62c",
        photoByteArray,
        -1,
        -1)
  }


  override fun onImageVisionDataReceived(visionConcepts: List<String>) {
    logMethod()
    getView()?.let {
      it.renderNonTranslatedWords(visionConcepts)
      requestTranslation(visionConcepts)
      for (text in visionConcepts)
        getView()?.speakText(text)
    }
  }

  override fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }

  override fun requestTranslation(textsToTranslate: List<String>) {
    logMethod()
    model.requestTranslation("en-it", textsToTranslate.subListSafe(0, 4))
  }

  override fun onTranslationDataReceived(translatedText: List<String>) {
    logMethod()
    getView()?.renderTranslatedWords(translatedText)
  }

  override fun onTranslationDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }


}