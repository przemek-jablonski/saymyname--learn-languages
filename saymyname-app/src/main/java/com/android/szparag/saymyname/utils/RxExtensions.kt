package com.android.szparag.saymyname.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 07/08/2017.
 */

fun CompositeDisposable.add(disposable: Disposable?): Boolean {
  disposable?.let {
    this.add(disposable)
    return false
  }
  return true
}