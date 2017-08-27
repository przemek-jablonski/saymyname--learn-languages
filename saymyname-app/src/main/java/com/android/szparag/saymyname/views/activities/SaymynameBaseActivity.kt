package com.android.szparag.saymyname.views.activities

import android.support.v7.app.AppCompatActivity
import com.android.szparag.saymyname.presenters.Presenter
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.View
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
abstract class SaymynameBaseActivity<P : Presenter<*>>: AppCompatActivity(), View {

  lateinit open var presenter: P
  val viewReadySubject: Subject<Boolean> = ReplaySubject.create()

  override fun onStart() {
    super.onStart()
    logMethod()
    setupViews()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    logMethod()
    super.onWindowFocusChanged(hasFocus)
    viewReadySubject.onNext(hasFocus)
  }

  override fun onViewReady(): Observable<Boolean> {
    return viewReadySubject
  }


}