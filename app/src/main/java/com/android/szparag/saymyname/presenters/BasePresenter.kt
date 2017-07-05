package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.presenters.contracts.Presenter
import com.android.szparag.saymyname.views.activities.RealtimeCameraPreviewActivity
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter <V : View> : Presenter <V> {

  override var view: V? = null

  override final fun attach(view: V) {
    super.attach(view)
  }

  override fun onAttached() {//...
  }


  override final fun detach() {
    super.detach()
  }

  override fun onBeforeDetached() {//...
  }

}