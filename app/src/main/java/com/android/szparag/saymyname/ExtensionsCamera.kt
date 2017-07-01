package com.android.szparag.saymyname

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */

fun Camera.takePicture(shutterCallback: ShutterCallback, pictureCallback: PictureBitmapCallback) {
  this.takePicture(
      shutterCallback,
      null,
      android.hardware.Camera.PictureCallback {
        data, camera ->
        pictureCallback.onPictureTaken(BitmapFactory.decodeByteArray(data, 0, data.size), camera)
      })
}

interface PictureBitmapCallback {
  fun onPictureTaken(bitmap: Bitmap?, camera: Camera)
}