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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/24/2017.
 */

/**
 * @return Point that contains coordinates (x, y) / (width, height) for the top-left corner
 * of views Bounding Box.
 *
 * To get bottom-right corner as well, use {@link #getBoundingBox()}
 */
inline fun View.getCoordinatesLeftTop(): Point {
  val outLocation: IntArray = kotlin.IntArray(2)
  this.getLocationOnScreen(outLocation)
  return Point(outLocation[0], outLocation[1])
}

/**
 * @return Rectangle containing coordinates of top-left and bottom-right corners
 * of view's Bounding Box.
 *
 * Typically, if view is positioned so that it's part is outside of the screen space
 * (screen is clipping the view), returning Rectangle values will be clipped to the screens
 * or parent view dimensions.
 *
 * Eg. Rect(120,20 - 240,190), when view is not clipped by the screen
 * Eg. Rect(120,20 - 1920,1080), when view is clipped by the screen with FullHD resolution.
 * Amount of clipping can be obtained with #getClipping()
 *
 */
inline fun View.getBoundingBox(): Rect {
  val rect = Rect()
  this.getGlobalVisibleRect(rect)
  return rect
}

/**
 *  @return Pair of BoundingBoxSpread dimensions (x,y) / (width,height), which is basically
 *  measurement of how much pixels does the Views Bounding Box take in X and Y axis.
 */
inline fun View.getBoundingBoxSpread(boundingBox: Rect = this.getBoundingBox(),
    divider: Float = 1f): Pair<Int, Int> {
  return Pair(
      boundingBox.width().div(divider).toInt(),
      boundingBox.height().div(divider).toInt()
  )
}

/**
 * Sisterly method to {@see #getBoundingBox()}, but the Bounding Box coordinates are not susceptible
 * to being modified or cut due to the parent / screen clipping.
 *
 * Eg. Rect(120,20 - 240,190), when view is not clipped by the screen
 * Eg. Rect(120,20 - 2220,1400), EVEN IF view is being clipped by the screen with FullHD resolution.
 *
 * @return Rectangle containing coordinates of top-left and bottom-right corners
 * of view's raw Bounding Box.
 */
inline fun View.getRawBoundingBox(boundingBox: Rect = this.getBoundingBox()): Rect {
  val rawBoundingBoxSpread = this.getRawBoundingBoxSpread()
  val rawBoundingBox = Rect(boundingBox)
  rawBoundingBox.right = boundingBox.left + rawBoundingBoxSpread.first
  rawBoundingBox.bottom = boundingBox.top + rawBoundingBoxSpread.second
  return rawBoundingBox
}

/**
 * Similar to @see #getBoundingBoxSpread(), but the calculations are done based on RAW (original)
 * Bounding Box.
 *
 * @return Pair of BoundingBoxSpread dimensions (x,y) / (width,height).
 */
inline fun View.getRawBoundingBoxSpread(): Pair<Int, Int> {
  return Pair(measuredWidth, measuredHeight)
}

/**
 * Calculates how much of the view is clipped (how much of it is not actually visible for the user),
 * either because of the view is positioned to close to screen or parent view borders.
 *
 * @return Pair of coordinates in both dimensions, determines how many pixels in X and Y axis
 * are clipped for whatever reason (Pair<Int,Int> / Pair<x,y> / Pair<width,height>).
 */
//todo: what if view is clipped not from the right BUT FROM THE LEFT, TOP AND BOTTOM?
inline fun View.getClippingSpread(): Pair<Int, Int> {
  val rawBoundingBoxSpread = getRawBoundingBoxSpread()
  val boundingBoxSpread = getBoundingBoxSpread()
  return Pair(
      rawBoundingBoxSpread.first - boundingBoxSpread.first,
      rawBoundingBoxSpread.second - boundingBoxSpread.second
  )
}

/**
 * Calculates how much of the view is clipped (how much of it is not actually visible for the user),
 * either because of the view is positioned to close to screen or parent view borders.
 *
 * @return Clipping rectangle - Rect showing how many pixels of the View are being clipped,
 * from each of the four sides.
 */
inline fun View.getClippingRect(boundingBox: Rect = this.getBoundingBox()): Rect {
  val rawBoundingBox = getRawBoundingBox(boundingBox)
  return Rect(
      rawBoundingBox.left - boundingBox.left,
      rawBoundingBox.top - boundingBox.top,
      rawBoundingBox.right - boundingBox.right,
      rawBoundingBox.bottom - boundingBox.bottom
  )
}

inline fun View.getCoordinatesCenter(): Point {
  return Point(pivotX.toInt(), pivotY.toInt())
}

inline fun View.getCoordinatesCenterF(): PointF {
  return PointF(pivotX, pivotY)
}

inline fun View.setCoordinatesCenter(coordX: Int, coordY: Int) {
  this.setCoordinatesCenter(coordX.toFloat(), coordY.toFloat())
}

inline fun View.setCoordinatesCenter(coordX: Float, coordY: Float) {
  this.translationX = coordX
  this.translationY = coordY
}


fun View.fadeOut(toAlpha: Float = 0f, interpolator: Interpolator = LinearInterpolator(),
    durationMillis: Long = 500, animationStartCallback: () -> Unit,
    animationEndCallback: () -> Unit) {
  fade(1f, toAlpha, interpolator, durationMillis, animationStartCallback, animationEndCallback)
}

fun View.fadeIn(fromAlpha: Float = 0f, interpolator: Interpolator = LinearInterpolator(),
    durationMillis: Long = 500, animationStartCallback: () -> Unit,
    animationEndCallback: () -> Unit) {
  this.visibility = VISIBLE
  fade(fromAlpha, 1f, interpolator, durationMillis, animationStartCallback, animationEndCallback)
}

private fun View.fade(fromAlpha: Float, toAlpha: Float,
    interpolator: Interpolator = LinearInterpolator(), durationMillis: Long = 500,
    animationStartCallback: () -> Unit, animationEndCallback: () -> Unit) {
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
        pictureCallback.onPictureTaken(BitmapFactory.decodeByteArray(data, 0, data.size), data,
            camera)
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


//____________references:

/**
 * @see View.getGlobalVisibleRect
 * Extension / Reference for View original method, which is encapsulating outValues in itself.
 *
 * Reference for @see getBoundingBox method.
 */
inline fun View.getGlobalVisibleRect(): Rect {
  return getBoundingBox()
}


/**
 * @see View.getLocationOnScreen
 * Extension / Reference for View original method, which is encapsulating outValues in itself.
 *
 * Reference for @see getCoordinatesLeftTop method.
 */
inline fun View.getLocationOnScreen(): Point {
  return getCoordinatesLeftTop()
}