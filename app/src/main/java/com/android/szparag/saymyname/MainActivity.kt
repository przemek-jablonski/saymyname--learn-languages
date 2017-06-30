package com.android.szparag.saymyname


import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import hugo.weaving.DebugLog
import java.io.IOException
import android.app.Activity
import android.view.Surface


@DebugLog
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

  val cameraSurfaceView: SurfaceView by bindView(R.id.surfaceView)
  val buttonTakePhoto: Button by bindView(R.id.button)
  var cameraInstance: Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }


  override fun onResume() {
    super.onResume()
    Handler().postDelayed({
      val holder = cameraSurfaceView.holder
      holder.addCallback(this)
      holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

      cameraInstance = openBackCameraInstance()
      startCameraRealtimePreview()
    }, 750)

  }

  private fun openBackCameraInstance() : Camera? {
    try {
      return Camera.open()
    } catch (exc : RuntimeException) {
      //todo: ...logging, show error, whatever
      exc.printStackTrace()
    }
    return null
  }

  fun startCameraRealtimePreview() {
//    stopPreviewAndFreeCamera();

    cameraInstance?.let {
      val cameraSizes = (cameraInstance as Camera).parameters.supportedPreviewSizes
      try {
        (cameraInstance as Camera).takeIf { cameraInstance != null }?.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
          setCameraDisplayOrientation(this@MainActivity, 0, cameraInstance!!)
        }
      } catch (exc: IOException) {
        exc.printStackTrace()
      }
    }

    cameraInstance?.let { it.startPreview() }
  }

  fun setCameraDisplayOrientation(activity: Activity, cameraId: Int,
      camera: android.hardware.Camera) {
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)
    val rotation = activity.windowManager.defaultDisplay.rotation
    var degrees = 0
    when (rotation) {
      Surface.ROTATION_0 -> degrees = 0
      Surface.ROTATION_90 -> degrees = 90
      Surface.ROTATION_180 -> degrees = 180
      Surface.ROTATION_270 -> degrees = 270
    }

    var result: Int
    //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    // do something for phones running an SDK before lollipop
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360
      result = (360 - result) % 360 // compensate the mirror
    } else { // back-facing
      result = (info.orientation - degrees + 360) % 360
    }

    camera.setDisplayOrientation(result)
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    //...
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    //...
  }

  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    //...
  }

  private fun releaseCameraAndPreview() {
//    preview.setCamera(null) ?
    cameraInstance?.let {
      it.release()
    }
  }

  private fun stopPreviewAndFreeCamera() {
    cameraInstance?.let {
      it.stopPreview()
      it.release()
    }
  }



}
