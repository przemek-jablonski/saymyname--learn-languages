package com.android.szparag.saymyname.views.widgets

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.szparag.kugo.KugoLog


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/23/2017.
 */
@KugoLog
class FloatingWordTextView : TextView {
  private val INITIAL_VISIBILITY = GONE
  private val GRAVITY = Gravity.CENTER
  private val AUXILIARY_TEXTALLCAPS = false
  private val AUXILIARY_TEXTCOLOR = R.color.floatingwords_auxilliary_text_font
  private val AUXILIARY_TEXTSIZE = R.dimen.floating_words_font_normal
  private val AUXILIARY_SHADOWCOLOR = R.color.floatingwords_auxilliary_text_shadow
  private val AUXILIARY_SHADOWDXDY = 0.0f
  private val AUXILIARY_SHADOWRADIUS = 25.0f
  private val PRIMARY_TEXTALLCAPS = true
  private val PRIMARY_TEXTCOLOR = R.color.floatingwords_primary_text_font
  private val PRIMARY_TEXTSIZE = R.dimen.floating_words_font_big
  private val PRIMARY_SHADOWCOLOR = R.color.floatingwords_primary_text_shadow
  private val PRIMARY_SHADOWDXDY = 0.0f
  private val PRIMARY_SHADOWRADIUS = 25.0f

  private val FLOATINGWORD_TYPE_AUXILLIARY = 0
  private val FLOATINGWORD_TYPE_PRIMARY = 1
  private var wordType: Int = FLOATINGWORD_TYPE_AUXILLIARY


  constructor(context: Context?) : super(context)

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

    context
        ?.theme
        ?.obtainStyledAttributes(
            attrs, R.styleable.FloatingWordTextView, 0, 0)
        ?.let {
          wordType = it.getInteger(
              R.styleable.FloatingWordTextView_type, FLOATINGWORD_TYPE_AUXILLIARY)
        }
    applyWordType(wordType)
  }

  @KugoLog
  private fun applyWordType(wordType : Int?) {
    when (wordType) {
      FLOATINGWORD_TYPE_AUXILLIARY -> {
        setAllCaps(AUXILIARY_TEXTALLCAPS)
        setTextColor(ContextCompat.getColor(context, AUXILIARY_TEXTCOLOR))
        textSize = context.resources.getDimension(AUXILIARY_TEXTSIZE)
        setShadowLayer(
            AUXILIARY_SHADOWRADIUS,
            AUXILIARY_SHADOWDXDY,
            AUXILIARY_SHADOWDXDY,
            AUXILIARY_SHADOWCOLOR
        )
      }
      FLOATINGWORD_TYPE_PRIMARY -> {
        setAllCaps(PRIMARY_TEXTALLCAPS)
        setTextColor(ContextCompat.getColor(context, PRIMARY_TEXTCOLOR))
        textSize = context.resources.getDimension(PRIMARY_TEXTSIZE)
        setShadowLayer(
            PRIMARY_SHADOWRADIUS,
            PRIMARY_SHADOWDXDY,
            PRIMARY_SHADOWDXDY,
            PRIMARY_SHADOWCOLOR
        )
      }
    }
    gravity = GRAVITY
//    visibility = INITIAL_VISIBILITY
  }

}