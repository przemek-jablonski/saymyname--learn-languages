package com.android.szparag.saymyname.views.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.dagger.DaggerGlobalScopeWrapper
import com.android.szparag.saymyname.presenters.HistoricalEntriesPresenter
import com.android.szparag.saymyname.views.contracts.HistoricalEntriesView

class HistoricalEntriesActivity : SaymynameBaseActivity<HistoricalEntriesPresenter>(), HistoricalEntriesView {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_historical_entries)
  }

  override fun onStart() {
    super.onStart()
    DaggerGlobalScopeWrapper.getComponent(this).inject(this)
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
    super.onStop()
  }

}
