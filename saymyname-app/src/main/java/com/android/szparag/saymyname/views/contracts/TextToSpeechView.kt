package com.android.szparag.saymyname.views.contracts

import java.util.Locale

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
interface TextToSpeechView : View {

  fun initializeTextToSpeechClient(locale: Locale)
  fun speakText(textToSpeak: String, flushSpeakingQueue: Boolean = false)

}