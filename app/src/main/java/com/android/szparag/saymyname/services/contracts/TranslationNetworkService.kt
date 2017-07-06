package com.android.szparag.saymyname.services.contracts

import com.android.szparag.saymyname.retrofit.apis.ApiTranslationYandex

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface TranslationNetworkService : NetworkService {

  interface TranslationNetworkResult {
    fun onSucceeded(translatedTexts: List<String>)
    fun onFailed()
  }

  fun requestTextTranslation(
      textsToTranslate: List<String>,
      languagesPair: String,
      callback: TranslationNetworkResult
  )

}