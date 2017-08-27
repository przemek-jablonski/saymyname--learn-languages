package com.android.szparag.saymyname.presenters

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.views.contracts.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/4/2017.
 */
abstract class BasePresenter<V : View> : Presenter<V> {

  internal var view: V? = null
  lateinit private var viewDisposables: CompositeDisposable
  lateinit private var modelDisposables: CompositeDisposable


  override final fun attach(view: V) {
    this.view = view
    onAttached()
  }

  @CallSuper
  override fun onAttached() {
    viewDisposables = CompositeDisposable()
    modelDisposables = CompositeDisposable()
  }

  override final fun detach() {
    onBeforeDetached()
    view = null
  }

  @CallSuper
  override fun onBeforeDetached() {
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