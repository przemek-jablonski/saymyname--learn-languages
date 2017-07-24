package com.android.szparag.saymyname.utils

import android.util.Log

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */

val APPLICATION_TAG = "saymyname"
val LOG_EXTENSION_STACKTRACE_DESIRED_DEPTH = 4

fun logMethod(level: Int = Log.DEBUG, optionalString: String? = null) {
  //TODO: refactor that so that it uses kapt and generating code, this approach is CPU heavy
  Thread.currentThread().stackTrace[LOG_EXTENSION_STACKTRACE_DESIRED_DEPTH]
      ?.takeIf {
        true
        //todo: check if NOT in debug
      }
      ?.let {
        val className = it.className
        val methodName = it.methodName
        val lineNumber = it.lineNumber
        Log.println(level, APPLICATION_TAG, "$className.$methodName [$lineNumber] | " + (optionalString ?: ""))
      }
}