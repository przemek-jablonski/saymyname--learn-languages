package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.utils.logMethod
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.HistoricalEntriesView
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 27/08/2017.
 */
class SaymynameHistoricalEntriesPresenter(
    private val repository: ImagesWordsRepository
) : BasePresenter<HistoricalEntriesView>(), HistoricalEntriesPresenter {

  override fun onAttached() {
    super.onAttached()
    repository.fetchAllImages().ui().subscribeBy(
        onNext = {
          logMethod("onAttached.onNext, list: $it")
          view?.updateImagesList(it)
        },
        onComplete = { logMethod("onAttached.onComplete") },
        onError = { logMethod("onAttached.onError, throwable: $it") }
    )
  }

  override fun onViewReady() {

  }

  override fun subscribeViewUserEvents() {

  }
}