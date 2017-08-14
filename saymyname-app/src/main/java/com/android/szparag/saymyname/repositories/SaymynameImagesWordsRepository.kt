package com.android.szparag.saymyname.repositories

import android.util.Log
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.asFlowable
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.Sort

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {


  protected val realm: Realm by lazy { Realm.getDefaultInstance() } //todo: change getDefaultInstance() //todo: make Realm operations asynchronous
  private var imagesSubscription: Disposable? = null

  //  lifecycle:
  override fun attach() {
    logMethod()
    logRealmChanges()
  }

  override fun detach() {
    logMethod()
    realm.close()
    imagesSubscription?.dispose()
  }

  protected fun logRealmChanges() {
    //todo: refactor to
    logMethod()
    realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING).addChangeListener {
      realmResults, changeSet ->
      realmResults.forEachIndexed {
        index, image ->
        Log.d("ImagesWordsRepository", "[Image ($index)]: {${image.dateTime}},{${image.languageFrom}},{${image.languageTo}},{${image.model}}")
        image.words.forEachIndexed { i, word -> Log.d("ImagesWordsRepository", "[Image $index][Word $i]: $word")
        }
      }
    }
  }


  //  push operations:
  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String)
      : Completable {
    logMethod()
    return Completable.fromAction {
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod()
        realm.createObject(Image::class.java).set(
            System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model
        )
      })
    }
  }


  override fun pushWordsOriginal(wordsOriginal: List<String>): Completable {
    logMethod()
    return Completable.fromAction {
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod()
        wordsOriginal.forEach {
          val elem = realm.createObject(Word::class.java)
          elem.id = System.currentTimeMillis() //todo: what if user changes its system time?
          elem.original = it
          realm.where(Image::class.java).findAllSorted("dateTime",
              Sort.DESCENDING).first()  //todo: replace that with calling on reference 'latestImage'
              .words.add(elem)
        }
      })
    }
  }


  override fun pushWordsTranslated(wordsTranslated: List<String>): Completable {
    logMethod()
    return Completable.fromAction {
      realm.executeTransaction({
        logMethod()
        realm.where(Image::class.java).findAllSorted("dateTime",
            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
            .words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
      })
    }
  }


  override fun fetchAllImages(): Flowable<List<Image>> {
    return realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING).asFlowable().share().replay().autoConnect()
  }

  override fun fetchLastImage(): Flowable<Image> {
    return realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING).asFlowable().map { listImages -> listImages[0] }.skip(1)
  }

  override fun fetchAllWords(): Flowable<List<Word>> {
    return realm.where(Word::class.java).findAllSorted("id", Sort.DESCENDING).asFlowable()
  }
}