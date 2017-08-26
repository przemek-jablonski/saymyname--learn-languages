@file:Suppress("NOTHING_TO_INLINE")

package com.android.szparag.saymyname.utils

import java.util.LinkedList

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/7/2017.
 */

inline fun LinkedList<String>.filterOutStupidWords(inSize: Int = 7,
    outSize: Int = 3): List<String> {
  return this
      .subList(0, inSize)
      .filterNot {
        it == ("no person") ||
            it == "horizontal" ||
            it == ("vertical") ||
            it == ("control") ||
            it == ("offense") ||
            it == ("one") ||
            it == ("two") ||
            it == ("container") ||
            it == ("abstract") ||
            it == ("Luna") ||
            it == ("crescent") ||
            it == ("background") ||
            it == ("insubstantial")
      }
      .subList(0, outSize)
}

inline fun List<String>.subListSafe(indexStart: Int, indexEnd: Int): List<String> {
  return this.subList(if (this.size < indexStart - 1) this.size - 1 else indexStart,
      if (this.size < indexEnd) this.size else indexEnd)
}

inline fun <A, B> List<Pair<A, B>>.splitPairsFirst(): List<A> {
  return this.map { pair -> pair.first }
}


inline fun <A, B> List<Pair<A, B>>.splitPairsSecond(): List<B> {
  return this.map { pair -> pair.second }
}