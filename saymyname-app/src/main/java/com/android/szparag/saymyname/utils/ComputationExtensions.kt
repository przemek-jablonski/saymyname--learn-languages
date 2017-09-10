@file:Suppress("NOTHING_TO_INLINE", "DEPRECATION")

package com.android.szparag.saymyname.utils

import android.content.Context
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.support.annotation.ArrayRes
import android.widget.ArrayAdapter
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.events.PermissionEvent.PermissionResponse
import com.android.szparag.saymyname.events.PermissionEvent.PermissionResponse.PERMISSION_GRANTED
import com.android.szparag.saymyname.events.PermissionEvent.PermissionResponse.PERMISSION_GRANTED_ALREADY
import java.util.Random

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/24/2017.
 */

inline fun Camera.setRotation(degreesToRotate: Int) {
  logMethod(", degreesToRotate: $degreesToRotate")
  val parameters = this.parameters
  parameters.setRotation(degreesToRotate)
  this.parameters = parameters
}

inline fun getCameraHardwareInfo(cameraId: Int = 0): CameraInfo {
  logMethod(", cameraId: $cameraId")
  val info = CameraInfo()
  Camera.getCameraInfo(cameraId, info)
  logMethod(", cameraInfo: $info (for cameraId: $cameraId)")
  return info
}

inline fun Int.max(otherNumber: Int): Int {
  return if (this > otherNumber) this else otherNumber
}

inline fun Int.min(otherNumber: Int): Int {
  return if (this < otherNumber) this else otherNumber
}

inline fun Random.nextFloat(minInclusive: Float, maxExclusive: Float): Float {
  return lerp(minInclusive, maxExclusive, nextFloat())
}


inline fun lerp(val1: Float, val2: Float, alpha: Float): Float {
  return val1 + alpha * (val2 - val2)
}

inline fun <T> Iterable<Iterable<T>?>.flatten(): MutableList <out T> {
  val result = ArrayList<T>()
  this.forEach { iterable ->
    iterable?.let { result.addAll(iterable) }
  }
  return result
}

inline fun PermissionResponse.isGranted(): Boolean = this == PERMISSION_GRANTED || this == PERMISSION_GRANTED_ALREADY
inline fun PermissionResponse.isNotGranted(): Boolean = !this.isGranted()

inline fun Context.createArrayAdapter(@ArrayRes textArrayResId: Int): ArrayAdapter<CharSequence> {
  return ArrayAdapter
      .createFromResource(this, textArrayResId, android.R.layout.simple_spinner_item)
      .apply { this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
}
