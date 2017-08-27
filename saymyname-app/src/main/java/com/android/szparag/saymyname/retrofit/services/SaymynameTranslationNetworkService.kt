package com.android.szparag.saymyname.retrofit.services

import com.android.szparag.saymyname.retrofit.apis.ApiTranslationYandex
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class SaymynameTranslationNetworkService(
    private val retrofit: Retrofit,
    override val NETWORK_SERVICE_API_KEY: String)
  : TranslationNetworkService {

  private val networkApiClient: ApiTranslationYandex = initializeNetworkApiClient()

  private fun initializeNetworkApiClient(): ApiTranslationYandex {
    return retrofit.create(ApiTranslationYandex::class.java)
  }

  //todo: languagesPair should be handled here somehow
  override fun requestTextTranslation(texts: List<String>,
      languagePair: String): Observable<List<Pair<String, String>>> {
    return networkApiClient.translate(
        key = NETWORK_SERVICE_API_KEY,
        textToTranslate = texts,
        targetLanguagesPair = languagePair)
        .subscribeOn(Schedulers.single())
        .map { response -> response.toTranslatedPair(texts) }
  }

}