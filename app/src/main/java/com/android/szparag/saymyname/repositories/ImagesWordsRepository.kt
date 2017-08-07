package com.android.szparag.saymyname.repositories

import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.DataCallback
import io.reactivex.functions.Action

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/6/2017.
 */
interface ImagesWordsRepository {

  /**
   * Pushing image captured by the camera to the storage.
   * Primary intention for this method should be {@see com.android.szparag.saymyname.repositories.entities.Image} object creation.
   */
  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String)

  /**
   * Pushing image captured by the camera to the storage.
   * Primary intention for this method should be @see com.android.szparag.saymyname.repositories.entities.Image object creation.
   * Receives callback as a parameter to inform about push completion.
   */
  fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      callback: DataCallback<Image>?)

  /**
   * Pushing retrieved words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object, if exists in the storage.
   */
  fun pushWordsOriginal(wordsOriginal: Array<String>)

  /**
   * Pushing retrieved original words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object, if exists in the storage.
   * Receives callback as a parameter to inform about push completion.
   */
  fun pushWordsOriginal(wordsOriginal: Array<String>, callback: DataCallback<Image>?)

  /**
   * Pushing retrieved translated words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object, if exists in the storage.
   */
  fun pushWordsTranslated(wordsTranslated: Array<String>)

  /**
   * Pushing retrieved translated words to the storage.
   * By default, this should write (or overwrite) data stored in the last Image object, if exists in the storage.
   * Receives callback as a parameter to inform about push completion.
   */
  fun pushWordsTranslated(wordsTranslated: Array<String>, callback: DataCallback<Image>?)


  fun fetchAllImages(): List<Image>
  fun fetchAllImages(changeListener: DataCallback<List<Image>>): List<Image>

  fun fetchAllWords(): List<Word>
  fun fetchAllWords(changeListener: DataCallback<List<Word>>): List<Word>

  fun fetchLatestImage(): Image
  fun fetchLatestImage(changeListener: DataCallback<Image>): Image

  fun fetchLatestImageWords(): List<Word>
  fun fetchLatestImageWords(changeListener: DataCallback<List<Word>>): List<Word>
  //  lifecycle:
  fun create()

  fun destroy()


}