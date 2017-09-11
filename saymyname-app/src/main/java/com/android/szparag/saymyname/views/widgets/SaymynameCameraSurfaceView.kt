package com.android.szparag.saymyname.views.widgets

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.android.szparag.saymyname.events.CameraSurfaceEvent
import com.android.szparag.saymyname.events.CameraSurfaceEvent.CameraSurfaceEventType.SURFACE_CHANGED
import com.android.szparag.saymyname.events.CameraSurfaceEvent.CameraSurfaceEventType.SURFACE_CREATED
import com.android.szparag.saymyname.events.CameraSurfaceEvent.CameraSurfaceEventType.SURFACE_INITIALIZED
import com.android.szparag.saymyname.utils.ERROR_CAMERA_SURFACE_NULL
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 10/09/2017.
 */
class SaymynameCameraSurfaceView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
  : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

  private val surfaceEventsSubject = ReplaySubject.create<CameraSurfaceEvent>()

  fun subscribeForEvents():Observable<CameraSurfaceEvent> {
    return surfaceEventsSubject
  }

  fun initialize() {
    logMethod("belbel")
    holder.addCallback(this)
//    holder.setFormat(PixelFormat.RGBA_8888)
    holder.setFormat(PixelFormat.OPAQUE) //todo: do performance tests
    surfaceEventsSubject.onNext(CameraSurfaceEvent(SURFACE_INITIALIZED))
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    logMethod(", holder: $holder")
    surfaceEventsSubject.onNext(CameraSurfaceEvent(SURFACE_CREATED))
  }

  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    logMethod(", holder: $holder, format: $format, dimens: ($width x $height")
    holder?.let { surfaceEventsSubject.onNext(CameraSurfaceEvent(SURFACE_CHANGED)) } ?: surfaceEventsSubject.onError(ERROR_CAMERA_SURFACE_NULL)
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    logMethod(", holder: $holder")
    holder?.removeCallback(this)
//    surfaceEventsSubject.onNext(CameraSurfaceEvent(SURFACE_DESTROYED))
    surfaceEventsSubject.onComplete()
  }

}