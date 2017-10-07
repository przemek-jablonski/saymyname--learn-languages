package com.android.szparag.saymyname.events

import com.android.szparag.saymyname.presenters.Presenter.PermissionType

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 02/09/2017.
 */
data class PermissionEvent(val permissionType: PermissionType, val permissionResponse: PermissionResponse) {

  enum class PermissionResponse { PERMISSION_GRANTED,
    PERMISSION_GRANTED_ALREADY,
    PERMISSION_DENIED,
    PERMISSION_DENIED_FIRST_TIME,
    PERMISSION_DENIED_PERMANENTLY
  }
}