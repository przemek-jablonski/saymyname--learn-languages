package com.android.szparag.saymyname.presenters.contracts

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface Presenter<V : View> {

  var view : V?

  @CallSuper
  fun attach(view: V) {
    this.view = view
    onAttached()
  }

  fun onAttached()

  fun onViewReady()

  @CallSuper
  fun detach() {
    onBeforeDetached()
    view = null
  }
  fun onBeforeDetached()

}