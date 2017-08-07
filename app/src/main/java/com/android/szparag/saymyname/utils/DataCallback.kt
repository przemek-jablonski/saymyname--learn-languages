package com.android.szparag.saymyname.utils

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
interface DataCallback<in T> {
  fun onChange(data: T)
}