package com.android.szparag.saymyname.utils

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.INVALID_POSITION
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 9/6/2017.
 */
class SpinnerSelectionObservable(private val view: Spinner) : InitialValueObservable<String>() {

  override fun subscribeListener(observer: Observer<in String>?) {
    if (observer == null || !checkMainThread(observer)) { return }
    val listener = Listener(view, observer)
    view.onItemSelectedListener = listener
    observer.onSubscribe(listener)
  }


  override fun getInitialValue(): String {
    return view.selectedItem.toString()
  }

  internal class Listener(private val spinner: Spinner, private val observer: Observer<in String>)
    : MainThreadDisposable(), OnItemSelectedListener {

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
      if (!isDisposed) { observer.onNext(spinner.adapter.getItem(position).toString()) }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
    }

    override fun onDispose() {
      spinner.onItemSelectedListener = null
    }
  }

}