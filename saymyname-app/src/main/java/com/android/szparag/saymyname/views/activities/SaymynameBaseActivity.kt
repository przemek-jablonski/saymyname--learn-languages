package com.android.szparag.saymyname.views.activities

import android.content.Intent
import android.support.annotation.CallSuper
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

  @CallSuper
  override fun onStart() {
    super.onStart()
    logMethod()
    setupViews()
  }

  @CallSuper
  override fun setupViews() {
    logMethod()
  }

  override final fun onWindowFocusChanged(hasFocus: Boolean) {
    logMethod()
    super.onWindowFocusChanged(hasFocus)
    viewReadySubject.onNext(hasFocus)
  }

  override final fun onViewReady(): Observable<Boolean> {
    logMethod()
    return viewReadySubject
  }

  override fun <A : SaymynameBaseActivity<*>> startActivity(targetActivityClass: Class<A>) {
    logMethod("target: $targetActivityClass")
    startActivity(Intent(applicationContext, targetActivityClass))
  }

}