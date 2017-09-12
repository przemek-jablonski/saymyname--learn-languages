package com.android.szparag.saymyname.views.contracts

import android.view.MenuItem
import com.android.szparag.saymyname.events.PermissionEvent
import com.android.szparag.saymyname.presenters.Presenter.PermissionType
import com.android.szparag.saymyname.views.activities.SaymynameBaseActivity
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/3/2017.
 */
interface View {

  /**
   *  Collection of alert messages that can be rendered for the user in this app
   */
  enum class UserAlertMessage {
    CAMERA_PERMISSION_ALERT,
    INTERNET_CONNECTION_NOT_AVAILABLE,
    INTERNET_CONNECTION_WEAK,
    STORAGE_PERMISSION_ALERT
  }

  /**
   *  Collection of menu options that can be clicked / selected by the user in this app
   */
  enum class MenuOption {
    SETTINGS,
    ACHIEVEMENTS,
    TUTORIAL,
    ABOUT,
    UPGRADE_DONATE,
    OPEN_SOURCE,
    HELP_FEEDBACK
  }


  /**
   *  Perform additional logic if the View or it's Subviews require specific setup before usage.
   *  Triggered during onStart callback in {@see SaymynameBaseActivity}, if given activity
   *  subclasses that one.
   */
  fun setupViews()

  /**
   *  Sends events when View is visible to the user.
   *  Indicates that instantiation and layout measurement for the View is done
   *  and rendering phase has begun.
   *
   *  Base Activity {@see SaymynameBaseActivity} ensures implementation using ReplaySubject
   *  so that subscribers of this stream are always aware of View readiness,
   *  no matter when they've actually subscribed.
   */
  fun subscribeOnViewReady(): Observable<Boolean>

  /**
   *  Triggers start of another Activity. Contract omits providing applicationContext
   *  so that it can be used with non-Android based Presenters.
   */
  fun <A : SaymynameBaseActivity<*>> startActivity(targetActivityClass: Class<A>)

  /**
   *  Checks whether permission(s) from PermissionType group are granted or not
   *  on this device.
   */
  fun checkPermissions(vararg permissions: PermissionType)

  /**
   *  Requests permission(s) from PermissionType group.
   */
  fun requestPermissions(vararg permissions : PermissionType)

  /**
   *  Returns stream of PermissionEvents informing about permission changes.
   */
  fun subscribeForPermissionsChange(): Observable<PermissionEvent>

  /**
   *  Renders alert message for the user, based on collection of messages defined in
   *  the UserAlertMessage enum.
   */
  fun renderUserAlertMessage(userAlertMessage: UserAlertMessage)

  /**
   *  Stops rendering current alert message
   */
  fun stopRenderUserAlertMessage(userAlertMessage: UserAlertMessage)

  fun subscribeMenuItemClicked(): Observable<MenuOption>

}