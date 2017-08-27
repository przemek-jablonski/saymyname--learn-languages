@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

import android.util.Log

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */

val APPLICATION_TAG = "saymyname"
val LOG_EXTENSION_STACKTRACE_DESIRED_DEPTH = 4

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
