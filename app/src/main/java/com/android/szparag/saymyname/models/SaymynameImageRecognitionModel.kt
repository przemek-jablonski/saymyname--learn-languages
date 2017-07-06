package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.models.contracts.ImageRecognitionModel
import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC
import com.android.szparag.saymyname.presenters.contracts.ImageProcessingPresenter
import com.android.szparag.saymyname.presenters.contracts.Presenter
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictResponse
import com.android.szparag.saymyname.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.services.contracts.ImageRecognitionNetworkService.ImageRecognitionNetworkResult
import com.android.szparag.saymyname.utils.logMethod

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class SaymynameImageRecognitionModel(
    override val service: ImageRecognitionNetworkService) : ImageRecognitionModel {

  private lateinit var presenter: ImageProcessingPresenter

  override fun attach(presenter: ImageProcessingPresenter) {
    logMethod()
    this.presenter = presenter
  }

  //todo: modelIds should be handled here somehow, not from outside
  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray) {
    logMethod()
    service.requestImageProcessing(
        modelId,
        imageByteArray,
        object : ImageRecognitionNetworkResult {

          override fun onSucceeded(concepts: List<Concept>) {
            //todo: persist to Realm first
            //todo: then query for the data (or not?)
            //todo: AND ONLY THEN:
            presenter?.onImageVisionDataReceived(concepts)
          }

          override fun onFailed() = presenter?.onImageVisionDataFailed(FAILURE_GENERIC)
        })
  }

}