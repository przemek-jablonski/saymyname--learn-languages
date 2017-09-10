@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

import android.util.Log

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */

val APPLICATION_TAG = "saymyname"
val LOG_EXTENSION_STACKTRACE_DESIRED_DEPTH = 4

val ERROR_CAMERA_RETRIEVAL by lazy { Throwable(ERROR_CAMERA_RETRIEVAL_KEY) }
val ERROR_CAMERA_CONFIGURATION_NULL by lazy { Throwable(ERROR_CAMERA_CONFIGURATION_NULL_KEY) }
val ERROR_CAMERA_RENDERING_COMMAND_NULL by lazy { Throwable(ERROR_CAMERA_RENDERING_COMMAND_NULL_KEY) }
val ERROR_CAMERA_RENDERING_COMMAND_EXC by lazy { Throwable(ERROR_CAMERA_RENDERING_COMMAND_EXC_KEY) }
val ERROR_COMPRESSED_BYTES_INVALID_SIZE by lazy {Throwable(ERROR_COMPRESSED_BYTES_INVALID_SIZE_KEY) }
val ERROR_REPOSITORY_PUSH_IMAGE_NULL by lazy {Throwable(ERROR_REPOSITORY_PUSH_IMAGE_NULL_KEY)}
val ERROR_IMAGEPROCESSINGWITHTRANSLATION_IMAGE_NULL by lazy {Throwable(ERROR_IMAGEPROCESSINGWITHTRANSLATION_IMAGE_NULL_KEY)}
private val ERROR_CAMERA_RETRIEVAL_KEY = "Error retrieving camera instance, camera object is null."
private val ERROR_CAMERA_RENDERING_COMMAND_NULL_KEY = "Error rendering stream from camera, camera is null."
private val ERROR_CAMERA_RENDERING_COMMAND_EXC_KEY = "Error rendering stream from camera, operation thrown exception."
private val ERROR_COMPRESSED_BYTES_INVALID_SIZE_KEY = "Error camera snapshot compression, byte count is 0 or lower."
private val ERROR_REPOSITORY_PUSH_IMAGE_NULL_KEY = "Error pushing to repository, parentImage is null (probably creating Image object errored)."
private val ERROR_IMAGEPROCESSINGWITHTRANSLATION_IMAGE_NULL_KEY = "Error during 'image processing with translation' request, captured image bytes are null."
private val ERROR_CAMERA_CONFIGURATION_NULL_KEY = "Error configuring camera, retrieved instance is null."

inline fun logMethod(optionalString: String? = null, level: Int = Log.DEBUG) {
  //TODO: refactor that so that it uses kapt and generating code, this approach is CPU heavy
  val currentThread = Thread.currentThread()
  currentThread
      ?.takeIf {
        true
        //todo: check if NOT in debug
      }
      ?.let {
        val stacktrace = it.stackTrace[LOG_EXTENSION_STACKTRACE_DESIRED_DEPTH]
        val threadName = it.name
        val className = stacktrace.className
        val methodName = stacktrace.methodName
        val lineNumber = stacktrace.lineNumber
        Log.println(level, APPLICATION_TAG, " [${threadName.toUpperCase()}] | $className.${methodName.toUpperCase()} [$lineNumber] | ${optionalString ?: ""}")
      }
}

inline fun logMethodError(optionalString: String? = null, level: Int = Log.ERROR){
  logMethod(optionalString, level)
}
