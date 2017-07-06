package com.android.szparag.saymyname.presenters.contracts

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.models.contracts.Model
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface Presenter {

  val model : Model

  fun attach(view: View)

  fun onAttached()

  fun onViewReady()

  fun detach()
  fun onBeforeDetached()

}