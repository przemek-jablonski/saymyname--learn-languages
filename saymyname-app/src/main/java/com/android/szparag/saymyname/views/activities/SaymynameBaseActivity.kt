package com.android.szparag.saymyname.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.CallSuper
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.events.PermissionEvent
import com.android.szparag.saymyname.events.PermissionEvent.PermissionResponse
import com.android.szparag.saymyname.presenters.Presenter
import com.android.szparag.saymyname.presenters.Presenter.PermissionType
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.views.contracts.View
import com.android.szparag.saymyname.views.contracts.View.UserAlertMessage
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
abstract class SaymynameBaseActivity<P : Presenter<*>> : AppCompatActivity(), View {

  lateinit open var presenter: P
  val viewReadySubject: Subject<Boolean> = ReplaySubject.create()
  val permissionsSubject: Subject<PermissionEvent> = PublishSubject.create()
  private var defaultUserAlert: Snackbar? = null

  @CallSuper
  override fun onStart() {
    super.onStart()
    logMethod()
    setupViews()
  }

  @CallSuper
  override fun onStop() {
    super.onStop()
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

  override fun onViewReady(): Observable<Boolean> {
    logMethod()
    return viewReadySubject
  }

  override fun <A : SaymynameBaseActivity<*>> startActivity(targetActivityClass: Class<A>) {
    logMethod("target: $targetActivityClass")
    startActivity(Intent(applicationContext, targetActivityClass))
  }

  override fun checkPermissions(vararg permissions: PermissionType) {
    permissions.forEach {
      val permissionResponseInt = checkSelfPermission(permissionTypeToString(it))
      permissionsSubject.onNext(PermissionEvent(it, permissionResponseToType(permissionResponseInt)))
    }
  }

  override fun requestPermissions(vararg permissions: PermissionType) {
    //...todo
  }

  override fun renderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    when(userAlertMessage) {
      View.UserAlertMessage.CAMERA_PERMISSION_ALERT -> {
        defaultUserAlert = Snackbar.make(window.decorView.rootView, resources.getString(R.string.dialog_alert_permission_camera), Snackbar.LENGTH_INDEFINITE)
        defaultUserAlert?.show()
      }
    }
  }

  override fun stopRenderUserAlertMessage() {
    defaultUserAlert?.dismiss()
  }

  override fun subscribeForPermissionsChange(): Observable<PermissionEvent> {
    return permissionsSubject
  }

  private fun permissionTypeToString(permissionType: PermissionType): String {
    when (permissionType) {
      Presenter.PermissionType.CAMERA_PERMISSION -> {
        return Manifest.permission.CAMERA
      }
      Presenter.PermissionType.STORAGE_ACCESS -> {
        return Manifest.permission_group.STORAGE
      }
    }
  }

  private fun permissionResponseToType(permissionResponseInt: Int): PermissionResponse {
    when (permissionResponseInt) {
      PackageManager.PERMISSION_GRANTED -> {
        return PermissionResponse.PERMISSION_GRANTED
      }
      else -> {
        return PermissionResponse.PERMISSION_DENIED
      }
    }
  }


}