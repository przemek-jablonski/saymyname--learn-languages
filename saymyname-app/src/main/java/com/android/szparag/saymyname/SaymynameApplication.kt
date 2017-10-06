package com.android.szparag.saymyname

import android.app.Application
import com.facebook.stetho.Stetho
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.log.LogLevel
import io.realm.log.RealmLog

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/2/2017.
 */
class SaymynameApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    Stetho.initializeWithDefaults(this)
    Realm.init(this)
    Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
    RealmLog.setLevel(LogLevel.ALL)
    RealmLog.registerDefaultLogger()
  }

}