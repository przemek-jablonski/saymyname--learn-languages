package com.android.szparag.saymyname.presenters.contracts

import com.android.szparag.saymyname.presenters.contracts.CameraPresenter.NetworkRequestStatus
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Model
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
//todo: split view (mainactivity) as well, so that there is CameraView and TranslationView
interface ImageProcessingPresenter: Presenter<RealtimeCameraPreviewView> {

  fun requestImageVisionData()
  fun onImageVisionDataReceived(visionConcepts: List<Concept>, model : Model)
  fun onImageVisionDataFailed(requestStatus: NetworkRequestStatus)

  fun requestTranslation()
  fun onTranslationDataReceived(translatedText: List<String>)
  fun onTranslationDataFailed(requestStatus: NetworkRequestStatus)
}