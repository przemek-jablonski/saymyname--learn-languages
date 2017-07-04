package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.presenters.contracts.Presenter
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter <V : View> : Presenter <V> {

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

  override var view: V?
    get() = TODO(
        "not implemented") //To change initializer of created properties use File | Settings | File Templates.
    set(value) {}
}