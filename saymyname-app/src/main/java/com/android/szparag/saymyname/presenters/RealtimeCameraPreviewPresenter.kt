package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.models.RealtimeCameraPreviewModel
import com.android.szparag.saymyname.views.contracts.RealtimeCameraPreviewView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface RealtimeCameraPreviewPresenter :
    CameraPreviewPresenter<RealtimeCameraPreviewView>,
    ImageRecognitionPresenter<RealtimeCameraPreviewView> {

  val model : RealtimeCameraPreviewModel

  fun subscribeNewWords()
}