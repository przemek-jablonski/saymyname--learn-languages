package com.android.szparag.saymyname.views.contracts

import com.android.szparag.saymyname.views.activities.SaymynameBaseActivity
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/3/2017.
 */
interface View {

  /**
   * Perform additional logic if the View or it's Subviews require specific setup before usage.
   * Triggered during onStart callback in {@see SaymynameBaseActivity}, if given activity
   * subclasses that one.
   */
  fun setupViews()

  /**
   * Sends events when View is visible to the user.
   * Indicates that instantiation and layout measurement for the View is done
   * and rendering phase has begun.
   *
   * Base Activity {@see SaymynameBaseActivity} ensures implementation using ReplaySubject
   * so that subscribers of this stream are always aware of View readiness,
   * no matter when they've actually subscribed.
   */
  fun onViewReady(): Observable<Boolean>

  /**
   * Triggers start of another Activity. Contract omits providing applicationContext
   * so that it can be used with non-Android based Presenters.
   */
  fun <A : SaymynameBaseActivity<*>> startActivity(targetActivityClass: Class<A>)

}