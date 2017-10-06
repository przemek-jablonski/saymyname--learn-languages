package com.android.szparag.saymyname.views.widgets

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.utils.Logger
import com.android.szparag.saymyname.utils.bindView

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 02/09/2017.
 */
class FullscreenMessageInfo @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  val messageIcon: ImageView by bindView(R.id.fullscreenMessageIcon)
  val messageText: TextView by bindView(R.id.fullscreenMessageText)
  private val logger = Logger.create(
      this::class) //todo: create class BaseWidget so that i can stash this Logger.create() there


  fun show(@DrawableRes messageDrawableRes: Int?, messageString: String) {
    logger.debug("show, messageDrawableRes: $messageDrawableRes, messageString: $messageString")
    messageDrawableRes?.let { messageIcon.setImageDrawable(resources.getDrawable(it)) }
    messageText.text = messageString
    visibility = View.VISIBLE
    //todo: fadein
  }

  fun show(@DrawableRes messageDrawableRes: Int?, @StringRes messageRes: Int) {
    logger.debug("show, messageDrawableRes: $messageDrawableRes, messageRes: $messageRes")
    show(messageDrawableRes, resources.getString(messageRes))
  }
}