package com.android.szparag.saymyname.presenters

import com.android.szparag.saymyname.repositories.ImagesWordsRepository
import com.android.szparag.saymyname.utils.ui
import com.android.szparag.saymyname.views.contracts.HistoricalEntriesView
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 27/08/2017.
 */
class SaymynameHistoricalEntriesPresenter(
    private val repository: ImagesWordsRepository
) : BasePresenter<HistoricalEntriesView>(), HistoricalEntriesPresenter {

  override fun onAttached() {
    super.onAttached()
    logger.debug("onAttached")
    repository
        .fetchAllImages()
        .ui()
        .subscribeBy(
            onNext = { imageList ->
              logger.debug("onAttached.repository.fetchAllImages.onNext, imageList: $imageList")
              view?.updateImagesList(imageList)
            },
            onError = { exc -> logger.error("onAttached.repository.fetchAllImages.onError", exc) },
            onComplete = { logger.debug("onAttached.repository.fetchAllImages.onComplete") }
        )
  }

  override fun onViewReady() {
    logger.debug("onViewReady")
  }
}