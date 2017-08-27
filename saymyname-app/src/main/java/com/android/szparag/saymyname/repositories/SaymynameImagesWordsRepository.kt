package com.android.szparag.saymyname.repositories

import android.util.Log
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.asFlowable
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.Sort

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {


  protected lateinit var realm: Realm  //todo: change getDefaultInstance() //todo: make Realm operations hronous
  private var imagesSubscription: Disposable? = null


  override fun attach(): Completable {
    logMethod(level = Log.WARN)
    return Completable.create({ emitter ->
      realm = Realm.getDefaultInstance()
      logRealmChanges(realm)
      emitter.onComplete()
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun detach(): Completable {
    return Completable.fromAction {
      logMethod(level = Log.WARN)
      realm.close()
      imagesSubscription?.dispose()
    }
  }

  private fun logRealmChanges(realm: Realm) {
    //todo: refactor to
    logMethod(level = Log.WARN)
    realm.addChangeListener { realm ->
      realm.where(Image::class.java).findAll().forEach {
        Log.d("ImagesWordsRepository", it.toString())
      }
    }
  }

  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      wordsOriginal: List<String>, wordsTranslated: List<String>): Completable {
    return Completable.fromAction {
      realm.executeTransaction { realm ->
        logMethod("thread: ${Thread.currentThread().name}")
        val parentImage = realm.createObject(Image::class.java).apply { this.set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model) }
        wordsOriginal.forEachIndexed { index, original ->
          parentImage.words.add(realm.createObject(Word::class.java).apply { this.set(System.currentTimeMillis(), original, wordsTranslated[index]) })
        }
      }
    }
  }

  override fun fetchAllImages(): Flowable<List<Image>> {
    return realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING)
        .asFlowable()
        .subscribeOn(AndroidSchedulers.mainThread())
  }

}