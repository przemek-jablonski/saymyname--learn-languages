package com.android.szparag.saymyname


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.szparag.saymyname.retrofit.apis.ApiImageRecognitionClarifai
import com.android.szparag.saymyname.retrofit.apis.ApiTranslationYandex
import com.android.szparag.saymyname.retrofit.models.imageRecognition.DataInput
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Image
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictResponse
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Input
import com.android.szparag.saymyname.retrofit.models.translation.TranslatedTextResponse
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
//      val holder = cameraSurfaceView.holder
//      holder.addCallback(this)
//      holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

//      cameraInstance = openBackCameraInstance()
//      startCameraRealtimePreview(cameraInstance)
//      setFocusMode(cameraInstance)
    }, 750)


    findViewById(R.id.button).setOnClickListener {
            takePicture(cameraInstance)
    }

  }


  private fun HANDLETRANSLATIONTEMPORARY(texts:List<String>) {
    val BASE_URL_YANDEX = "https://translate.yandex.net/api/v1.5/"
    val retrofitClientYandex = Retrofit.Builder()
        .baseUrl(BASE_URL_YANDEX)
        .client(OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceYandex = retrofitClientYandex.create(ApiTranslationYandex::class.java)
    val callTranslateText = apiServiceYandex.translate(
        key = getString(R.string.yandex_api_key),
        targetLanguagesPair = "en-it",
        textToTranslate = texts)
    callTranslateText.enqueue(object : Callback<TranslatedTextResponse> {
      override fun onResponse(call: Call<TranslatedTextResponse>, response: Response<TranslatedTextResponse>?) {
        Log.d("retrofit", "response, $call, $response")
        response?.body()?.texts?.let {
          (findViewById(R.id.translatedText1) as TextView).text = it.get(0)
          (findViewById(R.id.translatedText2) as TextView).text = it.get(1)
          (findViewById(R.id.translatedText3) as TextView).text = it.get(2)
        }
      }

      override fun onFailure(call: Call<TranslatedTextResponse>, t: Throwable) {
        Log.d("retrofit", "failure, $call, $t")
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
////    todo: put into dagger
    val BASE_URL_CLARIFAI = "https://api.clarifai.com/v2/"
    val retrofitClientClarifai = Retrofit.Builder()
        .baseUrl(BASE_URL_CLARIFAI)
        .client(OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServiceClarifai = retrofitClientClarifai.create(ApiImageRecognitionClarifai::class.java)
    val callProcessImageByGeneralModel = apiServiceClarifai.processImageByModel(
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
        Log.d("retrofit", "response, $call, $response")
        response?.body()?.let {
          val concepts = it.outputs?.get(0)?.dataOutput?.concepts

          HANDLETRANSLATIONTEMPORARY(listOf(concepts?.get(0)!!.name, concepts?.get(1)!!.name, concepts?.get(2)!!.name))
          concepts?.get(0)?.name?.let {
            conceptRenderToScreen(findViewById(R.id.englishText1) as TextView, it)
//            conceptTranslateToItalianAndRenderToScreen(findViewById(R.id.translatedText1) as TextView, it)
//            conceptTextToSpeech(it)
          }
          concepts?.get(1)?.name?.let {
            conceptRenderToScreen(findViewById(R.id.englishText2) as TextView, it)
//            conceptTranslateToItalianAndRenderToScreen(findViewById(R.id.translatedText2) as TextView, it)
//            conceptTextToSpeech(it)
          }
          concepts?.get(2)?.name?.let {
            conceptRenderToScreen(findViewById(R.id.englishText3) as TextView, it)
//            conceptTranslateToItalianAndRenderToScreen(findViewById(R.id.translatedText3) as TextView, it)
//            conceptTextToSpeech(it)
          }

        }
      }
    })

  }

  private fun conceptRenderToScreen(textView: TextView, englishText: CharSequence) {
    textView.text = englishText
  }

  //todo: must thow “Powered by Yandex.Translate”
  //todo: verify if other apis doesnt have similar requests
  //todo: add licences for open source libs

  @RequiresApi(VERSION_CODES.M) //todo: this is a huge problem!
  private fun conceptTranslateToItalianAndRenderToScreen(textView: TextView,
      englishText: CharSequence) {
    val processTextIntent = Intent()
        .setAction(Intent.ACTION_PROCESS_TEXT)
        .setType("text/plain")
    val queryIntentActivities = applicationContext.packageManager.queryIntentActivities(
        processTextIntent, 0)

    val get = queryIntentActivities?.get(0)

  }


  //todo: different languages https://stackoverflow.com/a/12251794/6942800
//  private fun conceptTextToSpeech(text: String, flushSpeakingQueue: Boolean = false) {
//    textToSpeechClient.speak(
//        text, if (flushSpeakingQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null)
//  }

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
//    try {
//      return Camera.open()
//    } catch (exc: RuntimeException) {
//      //todo: ...logging, show error, whatever
//      exc.printStackTrace()
//    }
//    return null
    return null
  }

  fun startCameraRealtimePreview(cameraInstance: Camera?) {
//    stopPreviewAndFreeCamera();
    cameraInstance?.let {
      cameraInstance.parameters.supportedPreviewSizes
      try {
        cameraInstance.run {
          it.setPreviewDisplay(cameraSurfaceView.holder)
//          setCameraDisplayOrientation(this@MainActivity, 0, it)
        }
      } catch (exc: IOException) {
        exc.printStackTrace()
        return
      }

      it.startPreview()
    }

//    cameraInstance?.let { it.startPreview() }
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
