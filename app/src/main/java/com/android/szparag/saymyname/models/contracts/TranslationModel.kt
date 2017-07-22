package com.android.szparag.saymyname.models.contracts

import com.android.szparag.saymyname.presenters.contracts.ImageProcessingPresenter

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface TranslationModel : Model{

  fun requestTranslation(languagePair : String, textsToTranslate : List<String>)
  fun attach(presenter: ImageProcessingPresenter)

}