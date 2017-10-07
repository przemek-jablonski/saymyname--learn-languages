package com.android.szparag.saymyname.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.events.PermissionEvent
import com.android.szparag.saymyname.events.PermissionEvent.PermissionResponse
import com.android.szparag.saymyname.presenters.Presenter
import com.android.szparag.saymyname.presenters.Presenter.PermissionType
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.CAMERA_PERMISSION
import com.android.szparag.saymyname.presenters.Presenter.PermissionType.STORAGE_ACCESS
import com.android.szparag.saymyname.utils.Logger
import com.android.szparag.saymyname.utils.bindView
import com.android.szparag.saymyname.views.contracts.View
import com.android.szparag.saymyname.views.contracts.View.MenuOption
import com.android.szparag.saymyname.views.contracts.View.MenuOption.ABOUT
import com.android.szparag.saymyname.views.contracts.View.MenuOption.ACHIEVEMENTS
import com.android.szparag.saymyname.views.contracts.View.MenuOption.HELP_FEEDBACK
import com.android.szparag.saymyname.views.contracts.View.MenuOption.OPEN_SOURCE
import com.android.szparag.saymyname.views.contracts.View.MenuOption.SETTINGS
import com.android.szparag.saymyname.views.contracts.View.MenuOption.TUTORIAL
import com.android.szparag.saymyname.views.contracts.View.MenuOption.UPGRADE_DONATE
import com.android.szparag.saymyname.views.contracts.View.UserAlertMessage
import com.jakewharton.rxbinding2.support.design.widget.RxNavigationView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
abstract class SaymynameBaseActivity<P : Presenter<*>> : AppCompatActivity(), View {

  internal lateinit var logger: Logger
  lateinit open var presenter: P
  val viewReadySubject: Subject<Boolean> = PublishSubject.create()
  val permissionsSubject: Subject<PermissionEvent> = ReplaySubject.create()
  internal val sideNavigationView: NavigationView by bindView(R.id.navigation_view)
  internal val parentDrawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
  private var defaultUserAlert: Snackbar? = null
  private var windowFocusCache = false

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewReadySubject.doOnSubscribe { viewReadySubject.onNext(hasWindowFocus()) }
    logger = Logger.create(this::class)
    logger.debug("logger created, $logger")
    logger.debug("onCreate, bundle: $savedInstanceState")
  }

  @CallSuper
  override fun onStart() {
    super.onStart()
    logger.debug("onStart")
    setupViews()
  }

  @CallSuper
  override fun onStop() {
    super.onStop()
    logger.debug("onStop")
  }

  @CallSuper
  override fun setupViews() = logger.debug("setupViews")

  override final fun onWindowFocusChanged(hasFocus: Boolean) {
    logger.debug("onWindowFocusChanged, hasFocus: $hasFocus, windowFocusCache: $windowFocusCache")
    super.onWindowFocusChanged(hasFocus)
    if (windowFocusCache != hasFocus) viewReadySubject.onNext(hasFocus)
    windowFocusCache = hasFocus
  }

  override fun subscribeOnViewReady(): Observable<Boolean> {
    logger.debug("subscribeOnViewReady")
    RxNavigationView.itemSelections(sideNavigationView)
    return viewReadySubject
  }

  override fun <A : SaymynameBaseActivity<*>> startActivity(targetActivityClass: Class<A>) {
    logger.debug("startActivity, target: $targetActivityClass")
    startActivity(Intent(applicationContext, targetActivityClass))
  }

  override fun subscribeMenuItemClicked(): Observable<MenuOption> {
    logger.debug("subscribeMenuItemClicked")
    return RxNavigationView.itemSelections(sideNavigationView)
        .doOnEach {
          parentDrawerLayout.closeDrawers()
          sideNavigationView
        }
        .map { it.toMenuOption() }
  }

  override fun checkPermissions(vararg permissions: PermissionType) {
    logger.debug("checkPermissions, permissions: $permissions")
    permissions.forEach {
      val permissionResponseInt = checkSelfPermission(permissionTypeToString(it))
      val permissionResponseToType = permissionResponseToType(permissionResponseInt)
      val permissionEvent = PermissionEvent(it, permissionResponseToType)
      permissionsSubject.onNext(permissionEvent)
    }
  }

  override fun requestPermissions(vararg permissions: PermissionType) {
    logger.debug("requestPermissions, permissions: $permissions")
    requestPermissions(
        permissions.map(this::permissionTypeToString).toTypedArray(),
        requestCode())
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    logger.debug(
        "onRequestPermissionsResult, requestCode: $requestCode, permissions: $permissions, results: $grantResults")
    if (requestCode == requestCode()) {
      permissions.forEachIndexed { index, permissionString ->
        permissionsSubject.onNext(
            PermissionEvent(
                permissionStringToType(permissionString),
                permissionResponseToType(grantResults[index]))
        )
      }
    }
  }

  override fun renderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("renderUserAlertMessage, alert: $userAlertMessage")
    when (userAlertMessage) {
      View.UserAlertMessage.CAMERA_PERMISSION_ALERT  -> {
        defaultUserAlert = Snackbar.make(window.decorView.rootView,
            resources.getString(R.string.dialog_alert_permission_camera),
            Snackbar.LENGTH_INDEFINITE)
        defaultUserAlert?.show()
      }
      View.UserAlertMessage.STORAGE_PERMISSION_ALERT -> {
        defaultUserAlert = Snackbar.make(window.decorView.rootView,
            resources.getString(R.string.dialog_alert_permission_storage),
            Snackbar.LENGTH_INDEFINITE)
        defaultUserAlert?.show()
      }
    }
  }

  override fun stopRenderUserAlertMessage(userAlertMessage: UserAlertMessage) {
    logger.debug("stopRenderUserAlertMessage, alert: $userAlertMessage")
    //omitting userAlertMessage check, since Snackbars can be dismissed manually any time.
    defaultUserAlert?.dismiss()
  }

  override fun subscribeForPermissionsChange(): Observable<PermissionEvent> {
    logger.debug("requestPermissions")
    return permissionsSubject
  }

  private fun permissionTypeToString(permissionType: PermissionType): String {
    logger.debug("permissionTypeToString, permissionType: $permissionType")
    return when (permissionType) {
      Presenter.PermissionType.CAMERA_PERMISSION -> {
        Manifest.permission.CAMERA
      }
      Presenter.PermissionType.STORAGE_ACCESS    -> {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      }
    }
  }

  private fun permissionStringToType(permissionString: String): PermissionType {
    logger.debug("permissionStringToType, permissionString: $permissionString")
    return when (permissionString) {
      Manifest.permission.CAMERA                 -> {
        CAMERA_PERMISSION
      }
      Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
        STORAGE_ACCESS
      }
      else                                       -> throw RuntimeException()
    }
  }

  private fun permissionResponseToType(permissionResponseInt: Int): PermissionResponse {
    logger.debug("permissionResponseToType, permissionResponseInt: $permissionResponseInt")
    return when (permissionResponseInt) {
      PackageManager.PERMISSION_GRANTED -> {
        PermissionResponse.PERMISSION_GRANTED
      }
      else                              -> {
        PermissionResponse.PERMISSION_DENIED
      }
    }
  }

  private fun requestCode() = Math.abs(this.packageName.hashCode())

  private fun MenuItem.toMenuOption(): MenuOption {
    when (this.itemId) {
      R.id.menu_action_settings      -> {
        return SETTINGS
      }
      R.id.menu_action_achievements  -> {
        return ACHIEVEMENTS
      }
      R.id.menu_action_tutorial      -> {
        return TUTORIAL
      }
      R.id.menu_action_about         -> {
        return ABOUT
      }
      R.id.menu_action_upgradedonate -> {
        return UPGRADE_DONATE
      }
      R.id.menu_action_opensource    -> {
        return OPEN_SOURCE
      }
      R.id.menu_action_helpfeedback  -> {
        return HELP_FEEDBACK
      }
      else                           -> {
        return SETTINGS
      }
    }
  }

}