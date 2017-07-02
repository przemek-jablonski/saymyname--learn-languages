package com.android.szparag.saymyname


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.szparag.saymyname.retrofit.apis.ApiClarifai
import com.android.szparag.saymyname.retrofit.models.DataInput
import com.android.szparag.saymyname.retrofit.models.Image
import com.android.szparag.saymyname.retrofit.models.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.models.ImagePredictResponse
import com.android.szparag.saymyname.retrofit.models.Input
import com.facebook.stetho.okhttp3.StethoInterceptor
import hugo.weaving.DebugLog
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale


@Suppress("DEPRECATION")
@DebugLog
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

  val cameraSurfaceView: SurfaceView by bindView(R.id.surfaceView)
  val buttonTakePhoto: Button by bindView(R.id.button)
  var cameraInstance: Camera? = null

  lateinit var textToSpeechClient: TextToSpeech

  //todo: remove this!
  var compressedPictureByteArray: ByteArray? = null
  var compressedPictureBitmap: Bitmap? = null

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


    findViewById(R.id.button).setOnClickListener {
      takePicture(cameraInstance)
    }

    textToSpeechClient = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
      status: Int ->
      status.takeIf { it != TextToSpeech.ERROR }?.run {
        textToSpeechClient.language = Locale.UK
      }
    })

  }


  private fun takePicture(cameraInstance: Camera?) {
    cameraInstance?.takePicture(
        ShutterCallback {
          onTakePictureShutterTriggered()
        },
        null,
        Camera.PictureCallback { data, camera ->
          onTakePictureByteArrayReady(data)
          compressedPictureByteArray?.let { sendPictureToClarifai(it) }
        }
    )
  }

  private fun onTakePictureShutterTriggered() {

  }

  private fun onTakePictureByteArrayReady(pictureByteArray: ByteArray) {
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

    compressedPictureBitmap = compressedBitmap
    compressedPictureByteArray = compressedByteStream.toByteArray()

//    Bitmap.createScaledBitmap()
//    options.inPurgeable = false

    (findViewById(R.id.imageView) as ImageView).setImageBitmap(compressedBitmap)
    findViewById(R.id.layout_camera_preview_realtime).visibility = View.GONE
    findViewById(R.id.layout_camera_photo_taken).visibility = View.VISIBLE
  }

  private fun sendPictureToClarifai(pictureByteArray: ByteArray) {
    //todo: put into dagger
    val BASE_URL_CLARIFAI = "https://api.clarifai.com/v2/"
    val retrofitClientClarifai = Retrofit.Builder()
        .baseUrl(BASE_URL_CLARIFAI)
        .client(
            OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceClarifai = retrofitClientClarifai.create(ApiClarifai::class.java)
    val callProcessImageByGeneralModel = apiServiceClarifai.processImageByGeneralModel(
        key = "Key " + getString(R.string.clarifai_api_key),
        modelId = getString(R.string.clarifai_model_id),
        imagePredictRequest = ImagePredictRequest(
            listOf(
                Input(DataInput(Image(Base64.encodeToString(pictureByteArray, Base64.DEFAULT))))))
    )

    callProcessImageByGeneralModel.enqueue(object : Callback<ImagePredictResponse> {
      override fun onFailure(call: Call<ImagePredictResponse>?, t: Throwable?) {
        Log.d("retrofit", "failure, $call, $t")
        (findViewById(R.id.englishText1) as TextView).text = "failed"
        (findViewById(R.id.englishText1) as TextView).text = ":("
        (findViewById(R.id.englishText1) as TextView).text = ":((("
      }

      override fun onResponse(call: Call<ImagePredictResponse>?,
          response: Response<ImagePredictResponse>?) {
        Log.d("retrofit", "failure, $call, $response")
        response?.body()?.let {
          val concepts = it.outputs?.get(0)?.dataOutput?.concepts
          concepts?.get(0)?.name?.let {
            (findViewById(R.id.englishText1) as TextView).text = it
            textToSpeech(it)
          }
          concepts?.get(1)?.name?.let {
            (findViewById(R.id.englishText2) as TextView).text = it
            textToSpeech(it)
          }
          concepts?.get(2)?.name?.let {
            (findViewById(R.id.englishText3) as TextView).text = it
            textToSpeech(it)
          }
        }
      }
    })

  }


  private fun textToSpeech(text: String, flushSpeakingQueue: Boolean = false) {
    textToSpeechClient.speak(
        text, if (flushSpeakingQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null)
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
