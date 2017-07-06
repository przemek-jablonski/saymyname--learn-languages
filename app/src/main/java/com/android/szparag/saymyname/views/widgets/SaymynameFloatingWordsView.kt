package com.android.szparag.saymyname.views.widgets

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.bindView
import com.android.szparag.saymyname.views.widgets.contracts.FloatingWordsView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
class SaymynameFloatingWordsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), FloatingWordsView {

  val auxiliaryWord1 : TextView by bindView(R.id.textview_word_1)
  val auxiliaryWord2: TextView by bindView(R.id.textview_word_2)
  val auxiliaryWord3 : TextView by bindView(R.id.textview_word_3)
  val primaryWord1 : TextView by bindView(R.id.textview_word_4)
  val primaryWord2 : TextView by bindView(R.id.textview_word_5)
  val primaryWord3 : TextView by bindView(R.id.textview_word_6)


  override fun renderAuxiliaryWords(auxiliaryWords: List<CharSequence>) {
    auxiliaryWord1.text = auxiliaryWords[0]
    auxiliaryWord2.text = auxiliaryWords[1]
    auxiliaryWord3.text = auxiliaryWords[2]
  }

  override fun renderPrimaryWords(primaryWords : List<CharSequence?>) {
    primaryWord1.text = primaryWords[0]
    primaryWord2.text = primaryWords[1]
    primaryWord3.text = primaryWords[2]
  }

}