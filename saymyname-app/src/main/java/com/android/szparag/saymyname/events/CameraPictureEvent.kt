package com.android.szparag.saymyname.events

import com.android.szparag.saymyname.events.CameraPictureEvent.CameraPictureEventType.CAMERA_BYTES_PROCESSED

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/15/2017.
 */
data class CameraPictureEvent(
    val type: CameraPictureEventType,
    val cameraImageBytes: ByteArray? = null
) {

  constructor(cameraImageBytes: ByteArray) : this(CAMERA_BYTES_PROCESSED, cameraImageBytes)

  enum class CameraPictureEventType {
    CAMERA_SHUTTER_EVENT,
    CAMERA_BYTES_RETRIEVED,
    CAMERA_BYTES_PROCESSED
  }
}