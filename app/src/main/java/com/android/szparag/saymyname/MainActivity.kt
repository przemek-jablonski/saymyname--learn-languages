package com.android.szparag.saymyname


import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import hugo.weaving.DebugLog
import java.io.IOException


@DebugLog
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

  val cameraSurfaceView: SurfaceView by bindView(R.id.surfaceView)
  val buttonTakePhoto: Button by bindView(R.id.button)
  var cameraInstance: Camera? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
//    val toolbar = findViewById(R.id.toolbar) as Toolbar
//    setSupportActionBar(toolbar)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }


  override fun onResume() {
    super.onResume()
    Handler().postDelayed(Runnable {
      val holder = cameraSurfaceView.holder
      holder.addCallback(this)
      holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

      safeCameraOpen(0)
      startCameraRealtimePreview()
    }, 2500)

  }

  fun startCameraRealtimePreview() {
//    stopPreviewAndFreeCamera();

    cameraInstance?.let {
      val cameraSizes = (cameraInstance as Camera).parameters.supportedPreviewSizes
      try {
        (cameraInstance as Camera).setPreviewDisplay(cameraSurfaceView.holder)
      } catch (exc: IOException) {
        exc.printStackTrace()
      }
    }

    cameraInstance?.let { it.startPreview() }
  }


  private fun safeCameraOpen(hardwareCameraId: Int): Boolean {
    var cameraOpen = false

    try {
//      releaseCameraAndPreview()
      cameraInstance = Camera.open(hardwareCameraId)
      cameraOpen = cameraInstance != null
    } catch (exc: Exception) {
      //... logging
      exc.printStackTrace()
    }

    return cameraOpen
  }

  private fun releaseCameraAndPreview() {
//    preview.setCamera(null) ?
    cameraInstance?.let {
      it.release()
    }
  }

  private fun stopPreviewAndFreeCamera() {
    cameraInstance?.let {
      it.stopPreview()
      it.release()
    }
  }

  override fun surfaceDestroyed(holder: SurfaceHolder?) {
    //...
  }

  override fun surfaceCreated(holder: SurfaceHolder?) {
    //...
  }

  override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    //...
  }

  //  override fun onOptionsItemSelected(item: MenuItem): Boolean {
//    // Handle action bar item clicks here. The action bar will
//    // automatically handle clicks on the Home/Up button, so long
//    // as you specify a parent activity in AndroidManifest.xml.
//    val id = item.itemId
//
//
//    if (id == R.id.action_settings) {
//      return true
//    }
//
//    return super.onOptionsItemSelected(item)
//  }
}
