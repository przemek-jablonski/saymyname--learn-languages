package com.android.szparag.saymyname


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback
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
import java.io.ByteArrayOutputStream
import java.io.IOException


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
//          PictureCallback { dataInput, camera ->
//            //...
//          }
//      )
//    }


    findViewById(R.id.button).setOnClickListener {
      takePicture(cameraInstance)
    }


//    findViewById(R.id.button).setOnClickListener {
//      cameraInstance?.takePicture(
//          ShutterCallback {
//
//          },
//          pictureCallback = object : PictureBitmapCallback {
//            override fun onPictureTaken(bitmap: Bitmap?, pictureBytesArray: ByteArray,
//                camera: Camera) {
//              bitmap?.let {
//                val orientation = CameraJavaUtils.getOrientation(pictureBytesArray)
//                var matrix = Matrix()
//                matrix.postRotate(orientation.toFloat())
//                val bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix,
//                    true)
////                bitmap.recycle()
//                (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap2)
//                findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
//                findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
//
//
//                //todo: put into dagger
//                val BASE_URL_CLARIFAI = "https://api.clarifai.com/v2/"
//                val retrofitClientClarifai = Retrofit.Builder()
//                    .baseUrl(BASE_URL_CLARIFAI)
//                    .client(
//                        OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build()
//
////                val apiClarifaiService = retrofitClientClarifai.create(ApiClarifai.class)
////                apiClarif
//
//                var compressedBitmapOutputStream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, compressedBitmapOutputStream)
//                val decodeByteArray = BitmapFactory.decodeByteArray(
//                    compressedBitmapOutputStream.toByteArray(), 0,
//                    compressedBitmapOutputStream.toByteArray().size)
//
//                var i = 1
//                val apiServiceClarifai = retrofitClientClarifai.create(ApiClarifai::class.java)
////                val callProcessImageByGeneralModel= apiServiceClarifai.processImageByGeneralModel(
////                    imagePredictRequest = ImagePredictRequest(listOf(
//////                        Input(DataInput(Image("https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Simple_Measuring_Cup.jpg/220px-Simple_Measuring_Cup.jpg"))))))
////                        Input(DataInput(Image(Base64.encodeToString(pictureBytesArray, Base64.DEFAULT)))))))
////
//////                        InputBase64(DataImageBase64(Base64.encodeToString(pictureBytesArray, Base64.DEFAULT))))))
////
////                callProcessImageByGeneralModel.enqueue(object : Callback<ImagePredictResponse> {
////                  override fun onFailure(call: Call<ImagePredictResponse>?, t: Throwable?) {
////                    Log.d("retrofit", "failure, $call, $t")
////                  }
////
////                  override fun onResponse(call: Call<ImagePredictResponse>?, response: Response<ImagePredictResponse>?) {
////                    Log.d("retrofit", "failure, $call, $response")
////                  }
////
////                })
//
//
//              }
//            }
//          }
//
////          PictureBitmapCallback({
////            bitmap, dataInput, camera ->
////            val orientation = CameraJavaUtils.getOrientation(dataInput)
////            (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap)
////            findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
////            findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
////          })
//      )
////      cameraInstance?.takePicture(
////          ShutterCallback {
////            //...
////          },
////          pictureCallback = object : PictureBitmapCallback {
////            override fun onPictureTaken(bitmap: Bitmap?, camera: Camera) {
////              if (bitmap != null) {
////                CameraJavaUtils.getOrientation()
////                (findViewById(R.id.imageView) as ImageView).setImageBitmap(bitmap)
////                findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
////                findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
////              } else {
////                //todo: throw error
////              }
////            }
////
////          }
////      )
//    }
  }


  private fun takePicture(cameraInstance: Camera?) {
    cameraInstance?.takePicture(
        ShutterCallback {
          onTakePictureShutterTriggered()
        },
        null,
        Camera.PictureCallback { data, camera ->
          onTakePictureByteArrayReady(data, camera)
        }
    )
  }

  private fun onTakePictureShutterTriggered() {

  }

  private fun onTakePictureByteArrayReady(pictureByteArray: ByteArray, camera: Camera) {
    val options = BitmapFactory.Options().apply {
      //      this.inJustDecodeBounds = true
      this.inPurgeable = true
      //todo: refactor so that i can specify minimum res (600-720px) instead of scaling
      //todo: because i do not know how powerful user camera is
      rescaleImageRequestFactor(8, this)
    }
    val originalByteStream = pictureByteArray
    val originalBitmap = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size)
    val scaledBitmap = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.size,
        options)
    val compressedByteStream = ByteArrayOutputStream()
    scaledBitmap.compress(JPEG, 60, compressedByteStream)
    val compressedBitmap = BitmapFactory.decodeByteArray(compressedByteStream.toByteArray(), 0,
        compressedByteStream.toByteArray().size)

//    Bitmap.createScaledBitmap()
//    options.inPurgeable = false

    (findViewById(R.id.imageView) as ImageView).setImageBitmap(compressedBitmap)
    findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
    findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
  }

  //todo: this kotlin syntax here really sucks, refactor!
  private fun rescaleImageRequestFactor(downScaleFactor: Int,
      bitmapOptions: BitmapFactory.Options): BitmapFactory.Options {
    bitmapOptions.inSampleSize = downScaleFactor
    return bitmapOptions
  }

//  private fun rescaleImageRequestMaxRes(resolutionMax: Int, bitmapOptions : BitmapFactory.Options) : BitmapFactory.Options{
//    bitmapOptions.inSampleSize = downScaleFactor
//    return bitmapOptions
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
