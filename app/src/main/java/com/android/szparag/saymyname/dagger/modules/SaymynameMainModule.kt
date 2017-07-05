package com.android.szparag.saymyname.dagger.modules

import com.android.szparag.saymyname.presenters.RealtimeCameraPreviewPresenter
import com.android.szparag.saymyname.presenters.contracts.RealtimeCameraPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */

@Module class SaymynameMainModule {

  @Provides @Singleton fun provideRealtimeCameraPresenter() : RealtimeCameraPresenter{
    return RealtimeCameraPreviewPresenter()
  }

}