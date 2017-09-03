package com.android.szparag.saymyname.presenters

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.views.contracts.View

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
interface Presenter<V : View> {

  /**
   * Permissions used by the application.
   */
  enum class PermissionType {
    CAMERA_PERMISSION,
    STORAGE_ACCESS
  }

  /**
   *  Attaches given View to this presenter, allowing two-way communication.
   *  Should be implemented in some abstract base Presenter as a final method.
   *
   *  Calls Presenter#onAttached() method when succeeded.
   */
  fun attach(view: V)

  /**
   *  Called when View attachment (#attach() method) was successful.
   *
   *  Should be left blank in abstract base Presenter, so that non-abstract
   *  classes extending the Presenter interface can perform additional mandatory setup operations
   *  like permission/network checks here.
   */
  fun onAttached()

  /**
   *  Detaching View from this Presenter.
   *  Should be called upon View decomposition (onStop / onDestroy methods etc.).
   *  Should be implemented in some abstract base Presenter as a final method.
   *
   *  Calls Presenter#onBeforeDetached() just before actually decomposing
   *  aggregated references and dependencies.
   */
  fun detach()

  /**
   *  Called just before View detachment operation (#detach() method) is completed.
   *
   *  Should be left blank in abstract base Presenter, so that non-abstract
   *  classes extending the Presenter interface can perform mandatory cleaning before
   *  View is detached and Presenter decomposed.
   */
  fun onBeforeDetached()

  /**
   *  Called when given View is fully instantiated and ready to perform actions
   *  ordered by the Presenter.
   */
  fun onViewReady()


}