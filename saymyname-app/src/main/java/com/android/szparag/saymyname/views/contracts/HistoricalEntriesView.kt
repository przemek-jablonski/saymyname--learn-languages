package com.android.szparag.saymyname.views.contracts

import com.android.szparag.saymyname.repositories.entities.Image

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/27/2017.
 */
interface HistoricalEntriesView: View {

  fun updateImagesList(imagesList: List<Image>)

}