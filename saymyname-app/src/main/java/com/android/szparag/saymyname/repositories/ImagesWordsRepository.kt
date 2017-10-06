package com.android.szparag.saymyname.repositories

import com.android.szparag.saymyname.repositories.entities.Image
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/6/2017.
 */
interface ImagesWordsRepository {

  //  lifecycle:
  fun attach(): Completable

  fun detach(): Completable

  fun pushImage(imageBase64: ByteArray, languageFrom: String, languageTo: String, model: String,
      wordsOriginal: List<String>, wordsTranslated: List<String>): Observable<Image>

  fun fetchAllImages(): Flowable<List<Image>>

}