package com.android.szparag.saymyname.views.widgets.overlays

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation.INFINITE
import android.view.animation.Animation.REVERSE
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.utils.bindViews
import com.android.szparag.saymyname.utils.fadeIn
import com.android.szparag.saymyname.utils.fadeInTranslate
import com.android.szparag.saymyname.utils.fadeOut
import com.android.szparag.saymyname.utils.getBoundingBox
import com.android.szparag.saymyname.utils.getBoundingBoxSpread
import com.android.szparag.saymyname.utils.getCoordinatesCenter
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.min
import com.android.szparag.saymyname.utils.nextFloat
import com.android.szparag.saymyname.utils.setCoordinatesCenter
import com.android.szparag.saymyname.utils.setCoordinatesCenterNoclip
import com.android.szparag.saymyname.views.widgets.FloatingWordTextView
import com.android.szparag.saymyname.views.widgets.contracts.FloatingWordsView
import java.util.Random

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */
class SaymynameFloatingWordsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), FloatingWordsView {

  val FLOATING_WORD_TEXT_TAG_PREFIX: String = "#"
  val FLOATING_WORD_SPAWN_BOUNDARIES_MARGIN_CUTOFF = 0.15f
  val PRIMARY_WORD_AUXILLIARY_OVERLAP_FACTOR = 0.40f

  val auxiliaryWordsViews: List<FloatingWordTextView> by bindViews(
      R.id.textview_word_auxilliary_1,
      R.id.textview_word_auxilliary_2,
      R.id.textview_word_auxilliary_3
  )
  val primaryWordsViews: List<FloatingWordTextView> by bindViews(
      R.id.textview_word_primary_1,
      R.id.textview_word_primary_2,
      R.id.textview_word_primary_3
  )

  var viewCenter: Point? = null
  var viewBoundingBox: Rect? = null
  var viewBoundingBoxSpawnSpread: Pair<Int, Int>? = null

  lateinit var random: Random


  override fun onFinishInflate() {
    super.onFinishInflate()
    logMethod()
    random = Random()
  }

  override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
    super.onWindowFocusChanged(hasWindowFocus)
    logMethod(optionalString = "hasWindowFocus: $hasWindowFocus")
    obtainRenderDimensions()
  }

  private fun obtainRenderDimensions() {
    logMethod()
    viewCenter = getCoordinatesCenter()
    viewBoundingBox = getBoundingBox()
    viewBoundingBox?.let {
      viewBoundingBoxSpawnSpread = getBoundingBoxSpread(
          it, 2f + FLOATING_WORD_SPAWN_BOUNDARIES_MARGIN_CUTOFF)
    }
  }

  override fun renderAuxiliaryWords(auxiliaryWords: List<CharSequence>) {
    logMethod(optionalString = auxiliaryWords.toString())
    logMethod(
        optionalString = "viewCenter: $viewCenter, viewBoundingBox: $viewBoundingBox, viewBoundingBoxSpawnSpread: $viewBoundingBoxSpawnSpread")

    if (viewBoundingBoxSpawnSpread == null || viewCenter == null) {
      logMethod(level = Log.ERROR,
          optionalString = "Render Dimensions are nulled out, returning...")
      return
    }

    val boundingBoxQuarters = (0..3).toMutableList()
    val iterationCount = IntRange(0, auxiliaryWords.size.min(auxiliaryWordsViews.size) - 1)
    logMethod(
        optionalString = "boundingBoxQuarters: $boundingBoxQuarters, iterationCount: $iterationCount")

    for (i in iterationCount) {
      boundingBoxQuarters
          .takeIf { it.isNotEmpty() }
          ?.let {
            val spawnQuarterId = it.removeAt(random.nextInt(it.size))
            val spawnCoordX = random.nextInt(viewBoundingBoxSpawnSpread!!.first)
            val spawnCoordY = random.nextInt(viewBoundingBoxSpawnSpread!!.second)
            logMethod(
                optionalString = "spawnQuarterId: $spawnQuarterId, spawn (x,y): $spawnCoordX, $spawnCoordY")
            when (spawnQuarterId) {
              0 -> {
                renderWord(
                    auxiliaryWordsViews[i],
                    auxiliaryWords[i],
                    (viewCenter!!.x - spawnCoordX).toFloat(),
                    (viewCenter!!.y + spawnCoordY).toFloat()
                )
              }
              1 -> {
                renderWord(
                    auxiliaryWordsViews[i],
                    auxiliaryWords[i],
                    (viewCenter!!.x - spawnCoordX).toFloat(),
                    (viewCenter!!.y - spawnCoordY).toFloat()
                )
              }
              2 -> {
                renderWord(
                    auxiliaryWordsViews[i],
                    auxiliaryWords[i],
                    (viewCenter!!.x + spawnCoordX).toFloat(),
                    (viewCenter!!.y + spawnCoordY).toFloat()
                )
              }
              3 -> {
                renderWord(
                    auxiliaryWordsViews[i],
                    auxiliaryWords[i],
                    (viewCenter!!.x + spawnCoordX).toFloat(),
                    (viewCenter!!.y - spawnCoordY).toFloat()
                )
              }
            }
          }
    }
  }


  override fun renderPrimaryWords(primaryWords: List<CharSequence?>) {
    auxiliaryWordsViews.forEachIndexed { i, auxiliaryWordView ->
      auxiliaryWordView.takeIf { it.visibility != GONE }
          ?.let {
            if (primaryWords[i] == null) return
            auxiliaryWordView.fadeOut(
                toAlpha = 35f,
                durationMillis = 2000,
                animationStartCallback = {
                  renderWord(
                      primaryWordsViews[i],
                      primaryWords[i]!!,
                      auxiliaryWordView.x,
                      auxiliaryWordView.y + auxiliaryWordView.getBoundingBox().height() * PRIMARY_WORD_AUXILLIARY_OVERLAP_FACTOR)
                },
                animationEndCallback = {
                })

          }
    }
  }

  private fun renderWord(wordView: TextView?, word: CharSequence, coordX: Float, coordY: Float) {
    logMethod(
        optionalString =
        "wordView : ${wordView?.let { it.resources.getResourceName(it.id) }}, " +
            "word: $word, " +
            "(x,y): ($coordX, $coordY)"
    )

    wordView?.let {
      it.text = FLOATING_WORD_TEXT_TAG_PREFIX.plus(word)
      it.fadeIn(
          animationStartCallback = {
            it.visibility = View.VISIBLE
//            it.setCoordinatesCenterNoclip(coordX, coordY)
          },
          animationEndCallback = {
//            val anim = TranslateAnimation(-10 * random.nextFloat(0.5f, 10f), 10 * random.nextFloat(0.5f, 20f), -1f, 1f)
//            anim.repeatMode = REVERSE
//            anim.repeatCount = INFINITE
//            anim.interpolator = AnticipateInterpolator(random.nextFloat(0.5f, 2.0f))
//            anim.duration = (3000 * (random.nextFloat(0.1f, 1.25f)).toLong())
//            anim.startTime = 0
//            it.animation = anim
          })
    }
  }

  override fun clearAuxillaryWords() {
    auxiliaryWordsViews.forEach { it.fadeOut(animationStartCallback = {}, animationEndCallback = {})}
  }

  override fun clearPrimaryWords() {
    primaryWordsViews.forEach { it.fadeOut(durationMillis = 1350, animationStartCallback = {}, animationEndCallback = {}) }
  }

  override fun clearWords() {
    clearAuxillaryWords()
    clearPrimaryWords()
  }

}