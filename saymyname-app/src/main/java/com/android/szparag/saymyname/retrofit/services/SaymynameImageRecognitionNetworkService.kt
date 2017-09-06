package com.android.szparag.saymyname.retrofit.services

import com.android.szparag.saymyname.retrofit.apis.ApiImageRecognitionClarifai
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService.ImageRecognitionModel
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.single
import io.reactivex.Observable
import retrofit2.Retrofit

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class SaymynameImageRecognitionNetworkService(
    private val retrofit: Retrofit,
    override val NETWORK_SERVICE_API_KEY: String)
  : ImageRecognitionNetworkService {

  private val AUTHORIZATION_KEY_PREFIX = "Key "
  private val networkApiClient: ApiImageRecognitionClarifai = initializeNetworkApiClient()

  private fun initializeNetworkApiClient(): ApiImageRecognitionClarifai {
    logMethod()
    return retrofit.create(ApiImageRecognitionClarifai::class.java)
  }


  override fun requestImageProcessing(modelId: String, image: ByteArray): Observable<List<Concept>> {
    logMethod("modelId: $modelId")
    if (modelId == ImageRecognitionModel.COLOURS.modelId) throw RuntimeException("Colours model is not available atm (different json structure)")
    return networkApiClient.processImageByModel(
        key = AUTHORIZATION_KEY_PREFIX + NETWORK_SERVICE_API_KEY,
        modelId = modelId,
        imagePredictRequest = ImagePredictRequest(image))
        .single()
        .map { response -> response.outputs.map { output -> output.dataOutput.concepts }.flatten() }
  }

}