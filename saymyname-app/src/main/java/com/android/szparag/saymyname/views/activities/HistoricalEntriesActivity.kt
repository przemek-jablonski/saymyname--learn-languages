package com.android.szparag.saymyname.views.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.android.szparag.saymyname.R
import com.android.szparag.saymyname.adapters.ImagesWordsRvAdapter
import com.android.szparag.saymyname.dagger.DaggerGlobalScopeWrapper
import com.android.szparag.saymyname.presenters.HistoricalEntriesPresenter
import com.android.szparag.saymyname.utils.bindView
import com.android.szparag.saymyname.views.contracts.HistoricalEntriesView
import javax.inject.Inject
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.SnapHelper
import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.utils.logMethod


class HistoricalEntriesActivity : SaymynameBaseActivity<HistoricalEntriesPresenter>(), HistoricalEntriesView {

  val recyclerViewHistorical: RecyclerView by bindView(R.id.recyclerview_historical_images_words)
  lateinit var recyclerViewAdapter: ImagesWordsRvAdapter
  @Inject lateinit override var presenter: HistoricalEntriesPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_historical_entries)
  }

  override fun onStart() {
    super.onStart()
    DaggerGlobalScopeWrapper.getComponent(this).inject(this)
    presenter.attach(this)
  }

  override fun setupViews() {
    super.setupViews()
    recyclerViewAdapter = ImagesWordsRvAdapter()
    recyclerViewHistorical.adapter = recyclerViewAdapter
    recyclerViewHistorical.layoutManager = LinearLayoutManager(this)
    recyclerViewHistorical.setHasFixedSize(true)
    LinearSnapHelper().attachToRecyclerView(recyclerViewHistorical)
  }

  //todo: i am sending entire list there, this may be not ideal in terms of performance
  override fun updateImagesList(imagesList: List<Image>) {
    logMethod("list: $imagesList")
    recyclerViewAdapter.setImages(imagesList)
    recyclerViewAdapter.notifyDataSetChanged()
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
    super.onStop()
  }

}
