@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/24/2017.
 */


inline fun Int.max(otherNumber: Int): Int {
  return if (this > otherNumber) this else otherNumber
}

inline fun Int.min(otherNumber: Int): Int {
  return if (this < otherNumber) this else otherNumber
}