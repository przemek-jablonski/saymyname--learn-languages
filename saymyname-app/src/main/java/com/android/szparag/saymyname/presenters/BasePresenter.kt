package com.android.szparag.saymyname.presenters

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter : Presenter {

  open var view: View? = null

  override final fun attach(view: View) {
    this.view = view
    onAttached()
  }

  @CallSuper
  override fun onAttached() {//...
  }


  override final fun detach() {
    onBeforeDetached()
    view = null
  }

  @CallSuper
  override fun onBeforeDetached() {//...
  }

}