package com.android.szparag.saymyname.presenters

import android.os.Handler
import com.android.szparag.saymyname.models.contracts.ImageRecognitionModel
import com.android.szparag.saymyname.models.contracts.TranslationModel
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.presenters.contracts.RealtimeCameraPresenter
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.subListSafe
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView
import com.szparag.kugo.KugoLog
import java.util.LinkedList

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
class RealtimeCameraPreviewPresenter(override val imageRecognitionModel: ImageRecognitionModel,
    override val translationModel: TranslationModel)
  : BasePresenter(), RealtimeCameraPresenter {


  fun getView() : RealtimeCameraPreviewView? {
    return this.view as RealtimeCameraPreviewView?
  }

  @KugoLog
  override fun onAttached() {
    super.onAttached()
    imageRecognitionModel.attach(this)
    translationModel.attach(this)
    initializeTextToSpeechClient()
  }

  @KugoLog
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
    imageRecognitionModel.requestImageProcessing(
        "aaa03c23b3724a16a56b629203edc62c",
        photoByteArray)
  }


  override fun onImageVisionDataReceived(visionConcepts: List<Concept>) {
    logMethod()
    //todo: move it to model
    var textsToTranslate : LinkedList<String> = visionConcepts.mapTo(LinkedList<String>()) { it.name }
    getView()?.let {
      val subList = textsToTranslate.subList(0, 7)
          .filterNot {
            it == ("no person") ||
                it == "horizontal" ||
                it == ("vertical") ||
                it == ("control") ||
                it == ("offense") ||
                it == ("one") ||
                it == ("two") ||
                it == ("container") ||
                it == ("abstract") ||
                it == ("Luna") ||
                it == ("crescent") ||
                it == ("background") ||
                it == ("insubstantial")
          }
          .subList(0, 3)
      it.renderNonTranslatedWords(subList)
      requestTranslation(subList)
      for (text in subList)
        getView()?.speakText(text)
    }
  }

  override fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus) {
    logMethod()
    //todo: make loading bar slowly stop and before that, change colour to red
  }

  override fun requestTranslation(textsToTranslate: List<String>) {
    logMethod()
    translationModel.requestTranslation("en-it", textsToTranslate.subListSafe(0, 4))
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