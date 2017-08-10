package com.android.szparag.saymyname.retrofit.services

import com.android.szparag.saymyname.retrofit.apis.ApiTranslationYandex
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
      languagePair: String): Observable<List<String>> {
    logMethod()
    return networkApiClient.translate(
        key = NETWORK_SERVICE_API_KEY,
        textToTranslate = texts,
        targetLanguagesPair = languagePair
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnEach {
          logMethod()
        }.map {
      response ->
      response.texts
    }
  }

}