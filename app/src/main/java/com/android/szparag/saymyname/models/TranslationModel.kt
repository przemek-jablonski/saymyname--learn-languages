package com.android.szparag.saymyname.models

import com.android.szparag.saymyname.presenters.ImageProcessingPresenter
import com.android.szparag.saymyname.retrofit.services.contracts.TranslationNetworkService.TranslationNetworkResult

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface TranslationModel : Model {

  fun requestTranslation(languagePair : String, textsToTranslate : List<String>, callback: TranslationNetworkResult)
  fun attach(presenter: ImageProcessingPresenter)

}