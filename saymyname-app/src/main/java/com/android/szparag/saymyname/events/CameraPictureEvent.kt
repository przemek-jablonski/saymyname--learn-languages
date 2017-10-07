package com.android.szparag.saymyname.events

import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_PROCESSED
import java.util.Arrays

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/15/2017.
 */
data class CameraPictureEvent(
    val type: CameraPictureEventType,
    val cameraImageBytes: ByteArray? = null
) {

  constructor(cameraImageBytes: ByteArray) : this(CAMERA_BYTES_PROCESSED, cameraImageBytes)

  enum class CameraPictureEventType { CAMERA_SHUTTER_EVENT,
    CAMERA_BYTES_RETRIEVED,
    CAMERA_BYTES_PROCESSED
  }

  //generated
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as CameraPictureEvent
    if (type != other.type) return false
    if (!Arrays.equals(cameraImageBytes, other.cameraImageBytes)) return false
    return true
  }

  //generated
  override fun hashCode(): Int {
    var result = type.hashCode()
    result = 31 * result + (cameraImageBytes?.let { Arrays.hashCode(it) } ?: 0)
    return result
  }
}