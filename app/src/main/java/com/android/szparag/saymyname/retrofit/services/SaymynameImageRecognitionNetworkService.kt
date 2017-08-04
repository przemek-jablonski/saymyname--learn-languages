package com.android.szparag.saymyname.retrofit.services

import android.util.Base64
import com.android.szparag.saymyname.retrofit.apis.ApiImageRecognitionClarifai
import com.android.szparag.saymyname.retrofit.models.imageRecognition.DataInput
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Image
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictResponse
import com.android.szparag.saymyname.retrofit.models.imageRecognition.Input
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionNetworkResult
import com.android.szparag.saymyname.utils.logMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class SaymynameImageRecognitionNetworkService(
    private val retrofit: Retrofit,
    override val NETWORK_SERVICE_API_KEY: String) : ImageRecognitionNetworkService {

  private val AUTHORIZATION_KEY_PREFIX = "Key "
  private val networkApiClient: ApiImageRecognitionClarifai by lazy { initializeNetworkApiClient() }

  private fun initializeNetworkApiClient(): ApiImageRecognitionClarifai {
    logMethod()
    return retrofit.create(ApiImageRecognitionClarifai::class.java)
  }


  override fun requestImageProcessing(modelId: String, imageByteArray: ByteArray,
      callback: ImageRecognitionNetworkResult) {
    logMethod()
    networkApiClient.processImageByModel(
        key = AUTHORIZATION_KEY_PREFIX + NETWORK_SERVICE_API_KEY,
        modelId = modelId,
        imagePredictRequest = ImagePredictRequest(//todo: this is ridiculous, refactor that somehow
            listOf(Input(DataInput(Image(Base64.encodeToString(imageByteArray, Base64.DEFAULT))))))
    ).enqueue(object : Callback<ImagePredictResponse> {
      override fun onResponse(
          call: Call<ImagePredictResponse>?,
          response: Response<ImagePredictResponse>?)
          = processSuccessfulResponse(response, callback)

      override fun onFailure(
          call: Call<ImagePredictResponse>?,
          t: Throwable?)
          = callback.onFailed()
    })
  }

  private fun processSuccessfulResponse(response: Response<ImagePredictResponse>?,
      callback: ImageRecognitionNetworkResult) {
    logMethod()
    val concepts = response?.body()?.getConcepts()
    if (concepts != null && concepts.isNotEmpty()) {
      callback.onSucceeded(concepts)
    } else {
      callback.onFailed()
    }
  }


}