package com.android.szparag.saymyname.views.contracts

import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
interface ImageRecognitionView : View {

  fun onUserTakePictureButtonClicked(): Observable<Any>
  fun renderNonTranslatedWords(nonTranslatedWords: List<String>)
  fun renderTranslatedWords(translatedWords: List<String>)
  fun stopRenderingWords()

}