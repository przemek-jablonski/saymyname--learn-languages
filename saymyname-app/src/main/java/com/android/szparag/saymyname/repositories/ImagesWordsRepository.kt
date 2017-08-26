package com.android.szparag.saymyname.repositories

import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/6/2017.
 */
interface ImagesWordsRepository {

  //  lifecycle:
  fun attach(): Completable

  fun detach() //todo: make this completable as well



//  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String): Observable<Image>
//
//  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String, wordsOriginal: List<String>): Observable<Image>

  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String, wordsOriginal: List<String>, wordsTranslated: List<String>)


//  /**
//   * Pushing retrieved words to the storage.
//   * By default, this should write (or overwrite) data stored in the last Image object,
//   * if exists in the storage.
//   */
//  fun pushWordsOriginal(wordsOriginal: List<String>): Observable<Image>
//
//  /**
//   * Pushing retrieved translated words to the storage.
//   * By default, this should write (or overwrite) data stored in the last Image object,
//   * if exists in the storage.
//   */
//  fun pushWordsTranslated(wordsTranslated: List<String>): Observable<Image>
//
//
//  fun fetchAllImages(): Flowable<List<Image>> //todo: refactor so that flowable generic has only Image in it (maybe?)
//
//  fun fetchLastImage(): Flowable<Image>
//
//  fun fetchAllWords(): Flowable<List<Word>>
//  fun logRealmChanges()
}