package com.android.szparag.saymyname.views.widgets.overlays

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.bindView
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.widgets.contracts.FloatingWordsView
import java.util.Random

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
class SaymynameFloatingWordsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), FloatingWordsView {

  val FLOATING_WORD_TEXT_TAG_PREFIX: String = "#"

  val auxiliaryWord1: TextView by bindView(R.id.textview_word_auxilliary_1)
  val auxiliaryWord2: TextView by bindView(R.id.textview_word_auxilliary_2)
  val auxiliaryWord3: TextView by bindView(R.id.textview_word_auxilliary_3)
  val primaryWord1: TextView by bindView(R.id.textview_word_primary_1)
  val primaryWord2: TextView by bindView(R.id.textview_word_primary_2)
  val primaryWord3: TextView by bindView(R.id.textview_word_primary_3)

  var screenWidth: Int? = null
  var screenHeight: Int? = null
  var floatingWordsSpawnDimensionMinX: Int = 0
  var floatingWordsSpawnDimensionMaxX: Int = 0
  var floatingWordsSpawnDimensionMinY: Int = 0
  var floatingWordsSpawnDimensionMaxY: Int = 0

  lateinit var random: Random


  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    logMethod()
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    logMethod()
    screenWidth = context.resources.displayMetrics.widthPixels
    screenWidth?.let {
      floatingWordsSpawnDimensionMinX = (it * 0.20).toInt()
      floatingWordsSpawnDimensionMaxX = (it * 0.60).toInt()
    }
    screenHeight = context.resources.displayMetrics.heightPixels
    screenHeight?.let {
      floatingWordsSpawnDimensionMinY = (it * 0.20).toInt()
      floatingWordsSpawnDimensionMaxY = (it * 0.60).toInt()
    }

    random = Random()
    logMethod(
        optionalString = "screenWidth: $screenWidth, screenHeight: $screenHeight, DimensionsX: <$floatingWordsSpawnDimensionMinX, $floatingWordsSpawnDimensionMaxX>, DimensionsY: <$floatingWordsSpawnDimensionMinY, $floatingWordsSpawnDimensionMaxY>)")
  }

  override fun renderAuxiliaryWords(auxiliaryWords: List<CharSequence>) {
    renderWord(
        auxiliaryWord1,
        auxiliaryWords[0],
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinX,
            floatingWordsSpawnDimensionMaxX),
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinY,
            floatingWordsSpawnDimensionMaxY)
    )
    renderWord(
        auxiliaryWord2,
        auxiliaryWords[1],
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinX,
            floatingWordsSpawnDimensionMaxX),
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinY,
            floatingWordsSpawnDimensionMaxY)
    )
    renderWord(
        auxiliaryWord3,
        auxiliaryWords[2],
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinX,
            floatingWordsSpawnDimensionMaxX),
        generateViewPosition(
            random,
            floatingWordsSpawnDimensionMinY,
            floatingWordsSpawnDimensionMaxY)
    )
  }

  override fun renderPrimaryWords(primaryWords: List<CharSequence?>) {
    primaryWords[0]?.let {
      renderWord(
          primaryWord1,
          it,
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinX,
              floatingWordsSpawnDimensionMaxX),
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinY,
              floatingWordsSpawnDimensionMaxY)
      )
    }
    primaryWords[1]?.let {
      renderWord(
          primaryWord2,
          it,
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinX,
              floatingWordsSpawnDimensionMaxX),
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinY,
              floatingWordsSpawnDimensionMaxY)
      )
    }
    primaryWords[2]?.let {
      renderWord(
          primaryWord3,
          it,
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinX,
              floatingWordsSpawnDimensionMaxX),
          generateViewPosition(
              random,
              floatingWordsSpawnDimensionMinY,
              floatingWordsSpawnDimensionMaxY)
      )
    }
  }


  private fun renderWord(wordView: TextView?, word: CharSequence, coordX: Float,
      coordY: Float) {
    logMethod(  optionalString = "wordView : $wordView, word: $word, (x,y): ($coordX, $coordY)") //todo: isnt it going to cause crash if wordview is null? just logging it?
    wordView?.let {
      if (it.visibility != View.VISIBLE)
        it.visibility = View.VISIBLE
      it.text = FLOATING_WORD_TEXT_TAG_PREFIX.plus(word)
      it.x = coordX
      it.y = coordY
    }
  }

  private fun generateViewPosition(random: Random, dimensionMinVal: Int,
      dimensionMaxVal: Int): Float {
    return (random.nextInt(dimensionMaxVal - dimensionMinVal) + dimensionMinVal).toFloat()
  }

  override fun clearAuxillaryWords() {
    auxiliaryWord1.visibility = View.GONE
    auxiliaryWord2.visibility = View.GONE
    auxiliaryWord3.visibility = View.GONE
  }

  override fun clearPrimaryWords() {
    primaryWord1.visibility = View.GONE
    primaryWord2.visibility = View.GONE
    primaryWord3.visibility = View.GONE
  }

  override fun clearWords() {
    clearAuxillaryWords()
    clearPrimaryWords()
  }

}