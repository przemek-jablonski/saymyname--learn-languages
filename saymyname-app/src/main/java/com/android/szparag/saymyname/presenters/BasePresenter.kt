package com.android.szparag.saymyname.presenters

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.utils.Logger
import com.android.szparag.saymyname.utils.add
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.View
import com.android.szparag.saymyname.views.contracts.View.MenuOption.ABOUT
import com.android.szparag.saymyname.views.contracts.View.MenuOption.ACHIEVEMENTS
import com.android.szparag.saymyname.views.contracts.View.MenuOption.HELP_FEEDBACK
import com.android.szparag.saymyname.views.contracts.View.MenuOption.OPEN_SOURCE
import com.android.szparag.saymyname.views.contracts.View.MenuOption.SETTINGS
import com.android.szparag.saymyname.views.contracts.View.MenuOption.TUTORIAL
import com.android.szparag.saymyname.views.contracts.View.MenuOption.UPGRADE_DONATE
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter<V : View> : Presenter<V> {

  internal lateinit var logger: Logger
  internal var view: V? = null
  lateinit private var viewDisposables: CompositeDisposable
  lateinit private var modelDisposables: CompositeDisposable


  override final fun attach(view: V) {
    logger = Logger.create(this::class)
    logger.debug("attach, view: $view")
    this.view = view
    onAttached()
  }

  @CallSuper
  override fun onAttached() {
    logger.debug("onAttached")
    viewDisposables = CompositeDisposable()
    modelDisposables = CompositeDisposable()
    subscribeViewReadyEvents()
    subscribeViewMenuEvents()
  }

  private fun subscribeViewReadyEvents() {
    logger.debug("subscribeViewReadyEvents")
    view
        ?.subscribeOnViewReady()
        ?.ui()
        ?.doOnSubscribe { logger.debug("subscribeViewReadyEvents.sub") }
        ?.filter { readyFlag -> readyFlag }
        ?.subscribeBy(
            onNext = { readyFlag ->
              logger.debug("subscribeViewReadyEvents.onNext, ready: $readyFlag")
              onViewReady()
            },
            onComplete = {
              logger.debug("subscribeViewReadyEvents.onComplete")
            },
            onError = { exc ->
              logger.error("subscribeViewReadyEvents.onError", exc)
            }
        )
  }

  private fun subscribeViewMenuEvents() {
    logger.debug("subscribeViewMenuEvents")
    view
        ?.subscribeMenuItemClicked()
        ?.ui()
        ?.subscribeBy(
            onNext = { menuOption ->
              when (menuOption) {
                SETTINGS -> {
                }
                ACHIEVEMENTS -> {
                }
                TUTORIAL -> {
                }
                ABOUT -> {
                }
                UPGRADE_DONATE -> {
                }
                OPEN_SOURCE -> {
                }
                HELP_FEEDBACK -> {
                }
              }
            }
        )
        .toViewDisposable()
  }


  override final fun detach() {
    logger.debug("detach")
    onBeforeDetached()
    view = null
  }

  @CallSuper
  override fun onBeforeDetached() {
    logger.debug("onBeforeDetached")
    viewDisposables.clear()
    modelDisposables.clear()
  }


  fun Disposable?.toViewDisposable() {
    logger.debug("toViewDisposable: viewDisposables: $viewDisposables, disposed: ${viewDisposables.isDisposed}")
    viewDisposables.takeIf { !it.isDisposed }?.add(this)
  }

  fun Disposable?.toModelDisposable() {
    logger.debug("toModelDisposable: modelDisposables: $modelDisposables, disposed: ${modelDisposables.isDisposed}")
    modelDisposables.takeIf { !it.isDisposed }?.add(this)
  }
}