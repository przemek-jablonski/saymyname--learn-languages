package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.CameraPresenter.NetworkRequestStatus.FAILURE_GENERIC
import com.android.szparag.saymyname.presenters.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionNetworkResult
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
//  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray) {
//    logMethod()
//    service.requestImageProcessing(
//        modelId,
//        imageByteArray,
//        object : ImageRecognitionNetworkResult {
//
//          override fun onSucceeded(concepts: List<Concept>) {
//            super.onSucceeded(concepts)
//            presenter?.onImageVisionDataReceived(concepts)
//          }
//
//          override fun onFailed() {
//            super.onFailed()
//            presenter?.onImageVisionDataFailed(FAILURE_GENERIC)
//          }
//        })
//  }

  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray,
      callback: ImageRecognitionNetworkResult) {
    logMethod()
//    service.requestImageProcessing(modelId, imageByteArray, callback)
  }

}