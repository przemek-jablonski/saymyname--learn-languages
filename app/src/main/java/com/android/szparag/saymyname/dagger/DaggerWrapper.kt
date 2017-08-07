package com.android.szparag.saymyname.dagger

import android.content.Context
import com.android.szparag.saymyname.dagger.components.DaggerSaymynameMainComponent
import com.android.szparag.saymyname.dagger.components.SaymynameMainComponent
import com.android.szparag.saymyname.dagger.modules.SaymynameMainModule

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class DaggerWrapper {

  //todo: find out if this is ok
  //todo: make dagger scoped
  companion object {
    private var component: SaymynameMainComponent? = null
    fun getComponent(context: Context) : SaymynameMainComponent{
      if (component == null) return constructComponent(context) else return component!!
    }
    private fun constructComponent(context: Context): SaymynameMainComponent {
      component = DaggerSaymynameMainComponent.builder()
          .saymynameMainModule(SaymynameMainModule(context))
          .build()
      return component!!
    }
  }

}