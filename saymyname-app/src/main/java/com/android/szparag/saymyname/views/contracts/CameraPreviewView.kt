package com.android.szparag.saymyname.views.contracts

import com.android.szparag.saymyname.events.CameraPictureEvent
import com.android.szparag.saymyname.events.CameraSurfaceEvent
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
interface CameraPreviewView : View {

  fun onUserTakePictureButtonClicked(): Observable<Any>
  //  fun renderRealtimeCameraPreview(): Observable<CameraSurfaceEvent>
  fun stopRenderingRealtimeCameraPreview()

  fun retrieveHardwareBackCamera(): Observable<Any>
  fun initializeCameraPreviewRendering(): Observable<CameraSurfaceEvent>
  fun scaleCompressEncodePictureByteArray(
      pictureByteArray: ByteArray): Observable<CameraPictureEvent>

  fun takePicture(): Observable<CameraPictureEvent>
  fun configureAndStartRealtimeCameraRendering()

}