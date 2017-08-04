package com.szparag.kugo

import javax.lang.model.element.TypeElement

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/4/2017.
 */
interface LogProcessor {

  fun writeToSource()
  fun processClass(type: TypeElement)
  fun processMethod(type: TypeElement)

}