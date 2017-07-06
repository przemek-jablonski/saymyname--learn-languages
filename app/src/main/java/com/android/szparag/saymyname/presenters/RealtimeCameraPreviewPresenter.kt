package com.android.szparag.saymyname.presenters

import android.os.Handler
import com.android.szparag.saymyname.models.contracts.ImageRecognitionModel
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.presenters.contracts.RealtimeCameraPresenter
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Model
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ModelVersion
import com.android.szparag.saymyname.retrofit.models.imageRecognition.OutputInfo
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Status
import com.android.szparag.saymyname.services.contracts.ImageRecognitionNetworkService.ImageRecognitionNetworkResult
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.android.szparag.saymyname.views.contracts.View
import java.util.LinkedList

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class RealtimeCameraPreviewPresenter(override val model: ImageRecognitionModel)
  : BasePresenter(), RealtimeCameraPresenter {


  fun getView() : RealtimeCameraPreviewView? {
    return this.view as RealtimeCameraPreviewView?
  }

  override fun onAttached() {
    super.onAttached()
    model.attach(this)
  }

  override fun onViewReady() {
    logMethod()
//    Handler().postDelayed(
    initializeCameraPreviewView()
//        1000)
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
        photoByteArray)
  }

  override fun onImageVisionDataReceived(visionConcepts: List<Concept>) {
    logMethod()
    //todo: move it to model
    val textsToTranslate = visionConcepts.mapTo(LinkedList<String>()) { it.name }
    requestTranslation(textsToTranslate)
  }

  override fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }

  override fun requestTranslation(textsToTranslate: List<String>) {
    logMethod()
    Handler().postDelayed({
      onTranslationDataReceived(listOf("asdadasd", "a", "adadasdasdas", "ad"))
    }, 2500)
  }

  override fun onTranslationDataReceived(translatedText: List<String>) {
    logMethod()
  }

  override fun onTranslationDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }


}