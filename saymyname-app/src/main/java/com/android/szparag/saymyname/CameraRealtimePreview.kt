package com.android.szparag.saymyname

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import hugo.weaving.DebugLog

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 6/23/2017.
 */
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
  }

  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
  }
}