package com.android.szparag.saymyname

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback



/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */

//todo: this is memory inneficient, think about different implementation
public fun Bitmap.createBitmap(source: Bitmap, angle: Float) : Bitmap {
  val matrix = Matrix()
  matrix.postRotate(angle)
  return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

fun Camera.takePicture(shutterCallback: ShutterCallback, pictureCallback: PictureBitmapCallback) {
  this.takePicture(
      shutterCallback,
      null,
      android.hardware.Camera.PictureCallback {
        data, camera ->
        pictureCallback.onPictureTaken(BitmapFactory.decodeByteArray(data, 0, data.size), data, camera)
      })
}



interface PictureBitmapCallback {
  fun onPictureTaken(bitmap: Bitmap?, pictureBytesArray: ByteArray, camera: Camera)
}