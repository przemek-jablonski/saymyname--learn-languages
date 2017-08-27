@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

import java.util.Random

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/24/2017.
 */


inline fun Int.max(otherNumber: Int): Int {
  return if (this > otherNumber) this else otherNumber
}

inline fun Int.min(otherNumber: Int): Int {
  return if (this < otherNumber) this else otherNumber
}

inline fun Random.nextFloat(minInclusive: Float, maxExclusive: Float): Float {
  return lerp(minInclusive, maxExclusive, nextFloat())
}


inline fun lerp(val1 : Float, val2: Float, alpha : Float) : Float {
  return val1 + alpha * (val2 - val2)
}

inline fun <T> Iterable<Iterable<T>?>.flatten(): MutableList <out T> {
    val result = ArrayList<T>()
    this.forEach{ iterable ->
      iterable?.let { result.addAll(iterable) }
    }
  return result
}

