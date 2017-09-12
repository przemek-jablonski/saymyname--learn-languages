package com.android.szparag.saymyname.retrofit.services.contracts

import android.support.annotation.CallSuper
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface TranslationNetworkService : NetworkService {

  enum class TranslationLanguage(val languageString: String, val languageCode: String) {
    ITALIAN("🇮🇹", "it"),
    SPANISH("🇪🇸", "es"),
    GERMAN("🇩🇪", "de"),
    ENGLISH("🇺🇸", "en"),
    POLISH("xxx", "pl") //todo polish
  }

  fun requestTextTranslation(texts: List<String>, languagePair: String): Observable<List<Pair<String, String>>>
}