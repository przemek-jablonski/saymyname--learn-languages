package com.android.szparag.saymyname.retrofit.apis

import com.android.szparag.saymyname.retrofit.entities.translation.AvailableLanguages
import com.android.szparag.saymyname.retrofit.entities.translation.TranslatedTextResponse
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
interface ApiTranslationYandex {

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
  ): Call<TranslatedTextResponse>

//  @Headers("Content-Type: application/x-www-form-urlencoded")
//  @POST("tr.json/translate")
//  fun translate(
//      @Query("key") key: String,
//      //      todo: make implementation for dual languages, like BELOW:
////      @Query("lang") languageFrom : String, languageTo:String
//      @Query("lang") targetLanguagesPair: String,
//      @Query("format") textFormat: String = "plain",
//      @Query("text") textToTranslate: String
//  ): Call<TranslatedTextResponse>

}