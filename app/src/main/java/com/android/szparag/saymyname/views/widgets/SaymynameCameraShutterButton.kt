package com.android.szparag.saymyname.views.widgets

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.widget.Button
import com.android.szparag.saymyname.views.widgets.contracts.CameraShutterButton

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/22/2017.
 */
class SaymynameCameraShutterButton: CameraShutterButton, FloatingActionButton {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr)



  override fun onShutterButtonClicked() {

  }

  override fun onShutterButtonLongClicked() {

  }

}