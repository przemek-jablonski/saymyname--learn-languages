package com.android.szparag.saymyname.retrofit.services

import com.android.szparag.saymyname.retrofit.apis.ApiImageRecognitionClarifai
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.services.contracts.ImageRecognitionNetworkService
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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


  override fun requestImageProcessing(model: String, image: ByteArray): Observable<List<Concept>> {
    logMethod()
    return networkApiClient.processImageByModel(
        key = AUTHORIZATION_KEY_PREFIX + NETWORK_SERVICE_API_KEY,
        modelId = model,
        imagePredictRequest = ImagePredictRequest(image)
    )
        .subscribeOn(Schedulers.single())
//        .observeOn(AndroidSchedulers.mainThread())
        .map { response -> response.outputs.map { output -> output.dataOutput.concepts }.flatten() }
  }

}