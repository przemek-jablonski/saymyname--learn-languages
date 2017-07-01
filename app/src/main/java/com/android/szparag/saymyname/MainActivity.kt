package com.android.szparag.saymyname


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback
import android.media.ExifInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import hugo.weaving.DebugLog
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream


@Suppress("DEPRECATION")
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

//      findViewById(R.layout.layout_camera_preview_realtime).visibility = View.VISIBLE
      cameraInstance = openBackCameraInstance()
      startCameraRealtimePreview(cameraInstance)
      setFocusMode(cameraInstance)
    }, 750)

//    findViewById(R.id.button).setOnClickListener {
//      cameraInstance?.takePicture(
//          ShutterCallback {
//            //...
//          },
//          null,
//          PictureCallback { data, camera ->
//            //...
//          }
//      )
//    }

    findViewById(R.id.button).setOnClickListener {
      cameraInstance?.takePicture(
          ShutterCallback {

          },
          pictureCallback = object : PictureBitmapCallback {
            override fun onPictureTaken(bitmap: Bitmap?, pictureBytesArray: ByteArray,
                camera: Camera) {
              bitmap?.let {
                val orientation = CameraJavaUtils.getOrientation(pictureBytesArray)
                var matrix = Matrix()
                matrix.postRotate(orientation.toFloat())
                val bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//                bitmap.recycle()
                (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap2)
                findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
                findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
              }
            }
          }

//          PictureBitmapCallback({
//            bitmap, data, camera ->
//            val orientation = CameraJavaUtils.getOrientation(data)
//            (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap)
//            findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
//            findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
//          })
      )
//      cameraInstance?.takePicture(
//          ShutterCallback {
//            //...
//          },
//          pictureCallback = object : PictureBitmapCallback {
//            override fun onPictureTaken(bitmap: Bitmap?, camera: Camera) {
//              if (bitmap != null) {
//                CameraJavaUtils.getOrientation()
//                (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap)
//                findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
//                findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
//              } else {
//                //todo: throw error
//              }
//            }
//
//          }
//      )
    }
  }

//  private fun decodeCameraInstanceBitmap(imageBytes: ByteArray): Bitmap {
//    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//
//  }

  private fun openBackCameraInstance(): Camera? {
    try {
      return Camera.open()
    } catch (exc: RuntimeException) {
      //todo: ...logging, show error, whatever
      exc.printStackTrace()
    }
    return null
  }

  fun startCameraRealtimePreview(cameraInstance: Camera?) {
//    stopPreviewAndFreeCamera();
    cameraInstance?.let {
      cameraInstance.parameters.supportedPreviewSizes
      try {
        cameraInstance.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
          setCameraDisplayOrientation(this@MainActivity, 0, it)
        }
      } catch (exc: IOException) {
        exc.printStackTrace()
      }

      it.startPreview()
    }

//    cameraInstance?.let { it.startPreview() }
  }

  private fun setFocusMode(cameraInstance: Camera?) {
    //todo: implement system that handles case where cam doesnt have this FocusMode
    cameraInstance?.let {
      val parameters = it.parameters
      parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
      it.parameters = parameters
    }
  }

  fun setCameraDisplayOrientation(activity: Activity, cameraId: Int,
      camera: android.hardware.Camera) {
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)
    val parameters = camera.parameters;
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

    parameters.setRotation(result)
    camera.parameters = parameters
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
