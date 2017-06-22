package com.android.szparag.saymyname

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import hugo.weaving.DebugLog

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 6/23/2017.
 */
@DebugLog
class CameraRealtimePreview : ViewGroup, SurfaceHolder.Callback {

  var cameraSurfaceView : SurfaceView? = null
  var cameraSurfaceHolder : SurfaceHolder? = null


  constructor(context : Context) : super(context) {
    cameraSurfaceView = SurfaceView(context)
    addView(cameraSurfaceView)

    cameraSurfaceHolder = (cameraSurfaceView as SurfaceView).holder
    (cameraSurfaceHolder as SurfaceHolder).addCallback(this)
    (cameraSurfaceHolder as SurfaceHolder).setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}