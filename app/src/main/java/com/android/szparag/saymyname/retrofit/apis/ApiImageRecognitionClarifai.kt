package com.android.szparag.saymyname.retrofit.apis

import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictRequest
import com.android.szparag.saymyname.retrofit.models.imageRecognition.ImagePredictResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
interface ApiImageRecognitionClarifai {

  //todo: rename to ApiImageRecognitionClarifai
  //todo: because i may want to change API provider someday

  @POST("models/{model_id}/outputs")
  fun processImageByModel(
    @Header("Authorization") key : String,
      @Path("model_id") modelId : String,
      @Body imagePredictRequest: ImagePredictRequest): Call<ImagePredictResponse>

}