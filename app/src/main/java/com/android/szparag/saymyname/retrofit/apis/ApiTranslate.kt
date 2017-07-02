package com.android.szparag.saymyname.retrofit.apis

import com.android.szparag.saymyname.retrofit.models.translation.AvailableLanguages
import com.android.szparag.saymyname.retrofit.models.translation.TranslateText
import com.android.szparag.saymyname.retrofit.models.translation.TranslatedText
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
interface ApiTranslate {

  @POST("tr.json/getLangs")
  fun getLanguages(
      @Query("key") key: String,
      @Query("ui") languageCode: String
  ): Call<AvailableLanguages>

  @Headers("Content-Type: application/x-www-form-urlencoded")
  @POST("tr.json/translate")
  fun translate(
      @Query("key") key: String,
      //      todo: make implementation for dual languages, like BELOW:
//      @Query("lang") languageFrom : String, languageTo:String
      @Query("lang") targetLanguagesPair: String,
      @Query("format") textFormat: String = "plain",
      @Query("text") textToTranslate: List<String>
  ): Call<TranslatedText>

  @Headers("Content-Type: application/x-www-form-urlencoded")
  @POST("tr.json/translate")
  fun translate(
      @Query("key") key: String,
      //      todo: make implementation for dual languages, like BELOW:
//      @Query("lang") languageFrom : String, languageTo:String
      @Query("lang") targetLanguagesPair: String,
      @Query("format") textFormat: String = "plain",
      @Query("text") textToTranslate: String
  ): Call<TranslatedText>

}