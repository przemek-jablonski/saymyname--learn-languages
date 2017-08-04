package com.szparag.kugo

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.annotation.AnnotationTarget.TYPE

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/4/2017.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class KugoLog
