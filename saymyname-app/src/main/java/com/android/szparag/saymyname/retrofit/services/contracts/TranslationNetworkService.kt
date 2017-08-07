package com.android.szparag.saymyname.retrofit.services.contracts

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.utils.logMethod

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface TranslationNetworkService : NetworkService {

  //todo: this should be generic with generic types
  interface TranslationNetworkResult {
    @CallSuper
    fun onSucceeded(translatedTexts: List<String>) {
      logMethod(optionalString = translatedTexts.toString())
    }
    @CallSuper
    fun onFailed() {
      logMethod()
    }
  }

  fun requestTextTranslation(
      textsToTranslate: List<String>,
      languagesPair: String,
      callback: TranslationNetworkResult
  )

}