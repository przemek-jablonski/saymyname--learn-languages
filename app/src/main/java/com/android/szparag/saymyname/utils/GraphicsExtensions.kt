@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.hardware.Camera
import android.hardware.Camera.ShutterCallback
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AlphaAnimation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/24/2017.
 */

inline fun View.getCoordinatesLeftTop() : Point {
  val outLocation : IntArray = kotlin.IntArray(2)
  this.getLocationOnScreen(outLocation)
  return Point(outLocation[0], outLocation[1])
}

inline fun View.getBoundingBox() : Rect {
  val rect = Rect()
  this.getGlobalVisibleRect(rect)
  return rect
}

inline fun View.getBoundingBoxSpread(boundingBox: Rect, divider : Float = 1f) : Pair<Int, Int> {
  return Pair(
      boundingBox.width().div(divider).toInt(),
      boundingBox.height().div(divider).toInt()
  )
}


fun View.getCoordinatesCenter() : Point {
  return Point(pivotX.toInt(), pivotY.toInt())
}

fun View.getCoordinatesCenterF() : PointF {
  return PointF(pivotX, pivotY)
}

fun View.setCoordinatesCenter(coordX : Int, coordY : Int) {
  this.setCoordinatesCenter(coordX.toFloat(), coordY.toFloat())
}

fun View.setCoordinatesCenter(coordX : Float, coordY : Float) {
  this.translationX = coordX
  this.translationY = coordY
}


fun View.fadeOut(toAlpha: Float = 0f, interpolator : Interpolator = LinearInterpolator(), durationMillis : Long = 500, animationStartCallback : () -> Unit, animationEndCallback: () -> Unit) {
  fade(1f, toAlpha, interpolator, durationMillis, animationStartCallback, animationEndCallback)
}

fun View.fadeIn(fromAlpha: Float = 0f, interpolator: Interpolator = LinearInterpolator(), durationMillis: Long = 500, animationStartCallback: () -> Unit, animationEndCallback: () -> Unit) {
  this.visibility = VISIBLE
  fade(fromAlpha, 1f, interpolator, durationMillis, animationStartCallback, animationEndCallback)
}

private fun View.fade(fromAlpha: Float, toAlpha: Float, interpolator: Interpolator = LinearInterpolator(), durationMillis: Long = 500, animationStartCallback: () -> Unit, animationEndCallback: () -> Unit) {
  val fade = AlphaAnimation(fromAlpha, toAlpha)
  fade.interpolator = interpolator
  fade.duration = durationMillis
  if (toAlpha != 1f) fade.fillAfter = true

  fade.setAnimationListener(object : AnimationListener {
    override fun onAnimationStart(animation: Animation) {
//      if (this@fade.visibility != VISIBLE) this@fade.visibility = VISIBLE
      animationStartCallback.invoke()
    }
    override fun onAnimationRepeat(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {
      animationEndCallback.invoke()
      if (toAlpha == 0f) this@fade.visibility = GONE
    }
  })

  this.startAnimation(fade)
}


//todo: this is memory inneficient, think about different implementation
public fun Bitmap.createBitmap(source: Bitmap, angle: Float): Bitmap {
  val matrix = Matrix()
  matrix.postRotate(angle)
  return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

//todo: this should not be as extension function, it does too much and has hardcoded shit in it
fun Camera.takePicture(shutterCallback: ShutterCallback, pictureCallback: PictureBitmapCallback) {
  this.takePicture(
      shutterCallback,
      null,
      Camera.PictureCallback {
        data, camera ->
        pictureCallback.onPictureTaken(BitmapFactory.decodeByteArray(data, 0, data.size), data, camera)
      })
}

//fun BitmapFactory.compressToStream(compressFormat : Bitmap.CompressFormat, qualityPercent : Int): ByteArrayOutputStream {
//  val compressedStream = ByteArrayOutputStream()
//  Bitma(compressFormat, qualityPercent, compressedStream)
//  return compressedStream
//}

//fun Bitmap.compress(compressFormat : Bitmap.CompressFormat, qualityPercent : Int): Bitmap? {
//  val compressedStream = ByteArrayOutputStream()
//  this.compress(compressFormat, qualityPercent, compressedStream)
//  return BitmapFactory.decodeByteArray(compressedStream.toByteArray(), 0, compressedStream.toByteArray().size)
//}


//
///**
// * This method is responsible for solving the rotation issue if exist. Also scale the images to
// * 1024x1024 resolution
// * @param context       The current context
// * @param selectedImage The Image URI
// * @return Bitmap image results
// * @throws IOException
// */
//fun Bitmap.adjustResolution(dimensionX: Int, dimensionY: Int): Bitmap {
//  val MAX_HEIGHT = dimensionX
//  val MAX_WIDTH = dimensionY
//
//  // First decode with inJustDecodeBounds=true to check dimensions
//  val options = BitmapFactory.Options()
//  options.inJustDecodeBounds = true
//  var imageStream = context.getContentResolver().openInputStream(selectedImage)
//  BitmapFactory.decodeStream(imageStream, null, options)
//  imageStream.close()
//
//  // Calculate inSampleSize
//  options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)
//
//  // Decode bitmap with inSampleSize set
//  options.inJustDecodeBounds = false
//  imageStream = context.getContentResolver().openInputStream(selectedImage)
//  var img = BitmapFactory.decodeStream(imageStream, null, options)
//
//  img = rotateImageIfRequired(img, selectedImage)
//  return img
//}


interface PictureBitmapCallback {
  fun onPictureTaken(bitmap: Bitmap?, pictureBytesArray: ByteArray, camera: Camera)
}