package com.android.szparag.saymyname.presenters

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.utils.add
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.View
import com.android.szparag.saymyname.views.contracts.View.MenuOption.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter<V : View> : Presenter<V> {

  internal var view: V? = null
  lateinit private var viewDisposables: CompositeDisposable
  lateinit private var modelDisposables: CompositeDisposable


  override final fun attach(view: V) {
    logMethod()
    this.view = view
    onAttached()
  }

  @CallSuper
  override fun onAttached() {
    logMethod()
    viewDisposables = CompositeDisposable()
    modelDisposables = CompositeDisposable()
    subscribeViewReadyEvents()
    subscribeViewMenuEvents()
  }

  private fun subscribeViewReadyEvents() {
    logMethod()
    view?.onViewReady()
        ?.ui()
        ?.doOnSubscribe { logMethod("subscribeViewReadyEvents.sub") }
        ?.filter { readyFlag -> readyFlag }
        ?.subscribeBy(
            onNext = { readyFlag ->
              logMethod("subscribeViewReadyEvents.onNext, ready: $readyFlag")
              onViewReady()
            }
        )
  }

  private fun subscribeViewMenuEvents() {
    logMethod()
    view?.subscribeMenuItemClicked()
        ?.ui()
        ?.subscribeBy (
            onNext = { menuOption ->
              when(menuOption) {
                SETTINGS -> { }
                ACHIEVEMENTS -> {}
                TUTORIAL -> {}
                ABOUT -> {}
                UPGRADE_DONATE -> {}
                OPEN_SOURCE -> {}
                HELP_FEEDBACK -> {}
              }
            }
        )
        .toViewDisposable()
  }


  override final fun detach() {
    logMethod()
    onBeforeDetached()
    view = null
  }

  @CallSuper
  override fun onBeforeDetached() {
    logMethod()
    viewDisposables.clear()
    modelDisposables.clear()
  }


  fun Disposable?.toViewDisposable() {
    viewDisposables.takeIf { !it.isDisposed }?.add(this)
  }

  fun Disposable?.toModelDisposable() {
    modelDisposables.takeIf { !it.isDisposed }?.add(this)
  }
}