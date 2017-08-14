package com.android.szparag.saymyname.repositories

import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/6/2017.
 */
interface ImagesWordsRepository {

  //  lifecycle:
  fun attach()

  fun detach()

  /**
   * Pushing image captured by the camera to the storage.
   * Primary intention for this method should be
   * {@see com.android.szparag.saymyname.repositories.entities.Image} object creation.
   */
  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String): Completable

  /**
   * Pushing retrieved words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object,
   * if exists in the storage.
   */
  fun pushWordsOriginal(wordsOriginal: List<String>): Completable

  /**
   * Pushing retrieved translated words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object,
   * if exists in the storage.
   */
  fun pushWordsTranslated(wordsTranslated: List<String>): Completable


  fun fetchAllImages(): Flowable<List<Image>> //todo: refactor so that flowable generic has only Image in it (maybe?)

  fun fetchLastImage(): Flowable<Image>

  fun fetchAllWords(): Flowable<List<Word>>
}