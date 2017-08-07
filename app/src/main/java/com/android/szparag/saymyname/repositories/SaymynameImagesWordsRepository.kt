package com.android.szparag.saymyname.repositories

import android.support.annotation.CallSuper
import android.util.Log
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.DataCallback
import com.android.szparag.saymyname.utils.logMethod
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {


  protected val realm: Realm by lazy { Realm.getDefaultInstance() } //todo: change getDefaultInstance() //todo: make Realm operations asynchronous
  private lateinit var allImages: RealmResults<Image>
  private lateinit var allWords: RealmResults<Word>

  //  lifecycle:
  override fun create() {
    logMethod()
    registerQueries()
  }

  override fun destroy() {
    logMethod()
    realm.close()
  }

  protected fun logRealmChanges() {
    allImages.forEachIndexed { index, image ->
      Log.d("ImagesWordsRepository", "[Image ($index)]: {${image.dateTime}},{${image.languageFrom}},{${image.languageTo}},{${image.model}}")
      image.words.forEachIndexed { i, word -> Log.d("ImagesWordsRepository", "[Words $i][@image $index]: $word") }

    }
  }

  //  registering database queries:
  @CallSuper
  protected open fun registerQueries() {
    logMethod()
    allImages = registerFetchAllImagesQuery()
    allWords = registerFetchAllWordsQuery()
  }

  protected open fun registerFetchAllImagesQuery(): RealmResults<Image>
      = realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING)


  protected open fun registerFetchAllWordsQuery(): RealmResults<Word>
      = realm.where(Word::class.java).findAllSorted("id", Sort.DESCENDING)


  //  push operations:
  override fun pushImage(
      imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String) {
    logMethod()
    pushImage(imageBase64, languageFrom, languageTo, model, null)
  }


  override fun pushWordsOriginal(wordsOriginal: Array<String>) {
    logMethod()
    pushWordsOriginal(wordsOriginal, null)
  }


  override fun pushWordsTranslated(wordsTranslated: Array<String>) {
    logMethod()
    pushWordsTranslated(wordsTranslated, null)
  }

  override fun pushImage(
      imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      callback: DataCallback<Image>?) {
    logMethod()
//    realm.executeTransactionAsync({
//      logMethod()
//      val elem = realm.createObject(Image::class.java)
//      elem.dateTime = System.currentTimeMillis()
//      elem.imageBase64 = imageBase64
//      elem.languageFrom = languageFrom
//      elem.languageTo = languageTo
//      elem.model = model
//    }, {
//      logMethod()
//      callback?.onChange(fetchLatestImage())
//      logRealmChanges()
//    }, {
//      logMethod()
//      logRealmChanges()
//    })
    realm.executeTransaction({
      logMethod()
      val elem = realm.createObject(Image::class.java)
      elem.dateTime = System.currentTimeMillis()
      elem.imageBase64 = imageBase64
      elem.languageFrom = languageFrom
      elem.languageTo = languageTo
      elem.model = model
      logRealmChanges()
      callback?.onChange(fetchLatestImage())
    })
  }

  override fun pushWordsOriginal(wordsOriginal: Array<String>,
      callback: DataCallback<Image>?) {
//    realm.executeTransactionAsync({
//      logMethod()
//      val latestImage = fetchLatestImage() //todo: use asynchronous!
//      wordsOriginal.forEach {
//        val elem = realm.createObject(Word::class.java)
//        elem.id = System.currentTimeMillis() //todo: what if user changes its system time?
//        elem.original = it
//        latestImage.words.add(elem)
//      }
//    }, {
//      logMethod()
//      callback?.onChange(fetchLatestImage())
//      logRealmChanges()
//    }, {
//      logMethod()
//      logRealmChanges()
//    })
    realm.executeTransaction({
      logMethod()
      val latestImage = fetchLatestImage() //todo: use asynchronous!
      wordsOriginal.forEach {
        val elem = realm.createObject(Word::class.java)
        elem.id = System.currentTimeMillis() //todo: what if user changes its system time?
        elem.original = it
        latestImage.words.add(elem)
      }
      logRealmChanges()
      callback?.onChange(fetchLatestImage())
    })

  }

  override fun pushWordsTranslated(wordsTranslated: Array<String>,
      callback: DataCallback<Image>?) {
    logMethod()
//    realm.executeTransaction({
//      logMethod()
//      val latestImage = fetchLatestImage() //todo: use asynchronous!
//      latestImage.words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
//    }, {
//      logMethod()
//      callback?.onChange(fetchLatestImage())
//      logRealmChanges()
//    }, {
//      logMethod()
//      logRealmChanges()
//    })
    realm.executeTransaction({
      logMethod()
      val latestImage = fetchLatestImage() //todo: use asynchronous!
      latestImage.words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
      logRealmChanges()
      callback?.onChange(fetchLatestImage())
    })
  }


  //  fetch operations:
  override fun fetchAllImages(): List<Image> = allImages


  override fun fetchAllWords(): List<Word> = allWords


  override fun fetchLatestImage(): Image = allImages.first()


  override fun fetchLatestImageWords(): List<Word> = allImages.first().words


  //todo: needs method unsubscribeFetchAllImages and so on, this will leak memory
  override fun fetchAllImages(changeListener: DataCallback<List<Image>>): List<Image> {
    logMethod()
    allImages.addChangeListener(changeListener::onChange)
    return allImages
  }

  override fun fetchAllWords(changeListener: DataCallback<List<Word>>): List<Word> {
    logMethod()
    allWords.addChangeListener(changeListener::onChange)
    return allWords
  }

  override fun fetchLatestImage(changeListener: DataCallback<Image>): Image {
    logMethod()
    allImages.addChangeListener { t -> changeListener.onChange(allImages.first()) }
    return allImages.first()
  }

  override fun fetchLatestImageWords(changeListener: DataCallback<List<Word>>): List<Word> {
    logMethod()
    allWords.addChangeListener { t -> changeListener.onChange(allImages.first().words) }
    return allImages.first().words
  }
}