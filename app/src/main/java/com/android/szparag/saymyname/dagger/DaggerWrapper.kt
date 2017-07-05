package com.android.szparag.saymyname.dagger

import com.android.szparag.saymyname.dagger.components.DaggerSaymynameMainComponent
import com.android.szparag.saymyname.dagger.components.SaymynameMainComponent
import com.android.szparag.saymyname.dagger.modules.SaymynameMainModule

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
class DaggerWrapper {
  companion object {
    val component: SaymynameMainComponent by lazy { constructComponent() }
    private fun constructComponent(): SaymynameMainComponent {
      return DaggerSaymynameMainComponent.builder()
          .saymynameMainModule(SaymynameMainModule())
          .build()
    }
  }

}