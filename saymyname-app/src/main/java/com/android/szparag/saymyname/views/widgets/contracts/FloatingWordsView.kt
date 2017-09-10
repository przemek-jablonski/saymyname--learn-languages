package com.android.szparag.saymyname.views.widgets.contracts


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
interface FloatingWordsView {

  fun renderLoadingHalo()
  fun stopRenderingLoadingHalo()

  fun renderAuxiliaryWords(auxiliaryWords: List<CharSequence>)
  fun renderPrimaryWords(primaryWords : List<CharSequence?>)

  fun clearAuxillaryWords()
  fun clearPrimaryWords()
  fun clearWords()
}