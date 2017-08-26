package com.android.szparag.saymyname.repositories

import android.util.Log
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.asFlowable
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import io.realm.log.RealmLogger

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {


  protected lateinit var realm: Realm  //todo: change getDefaultInstance() //todo: make Realm operations hronous
  private var imagesSubscription: Disposable? = null

  //  lifecycle:
  override fun attach(): Completable {
    logMethod(level = Log.WARN)
    return Completable.create({ emitter ->
      logMethod(level = Log.WARN)
      realm = Realm.getDefaultInstance()
      logRealmChanges(realm)
      emitter.onComplete()
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun detach() {
    logMethod(level = Log.WARN)
    realm.close()
    imagesSubscription?.dispose()
  }

  private fun logRealmChanges(realm: Realm) {
    //todo: refactor to
    logMethod(level = Log.WARN)
    realm.addChangeListener { Log.d("ImagesWordsRepository", realm.where(Image::class.java).findAll()) }
//    realm.where(Image::class.java)
//        .findAll()
//        .addChangeListener ({
//          realmResults, changeSet ->
//          realmResults.forEachIndexed { index, image ->
//            Log.d("ImagesWordsRepository",
//                "[Image ($index)]: {${image.dateTime}},{${image.languageFrom}},{${image.languageTo}},{${image.model}}")
//            image.words.forEachIndexed { i, word ->
//              Log.d("ImagesWordsRepository", "[Image $index][Word $i]: $word")
//            }
//          }
//        })
  }

  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      wordsOriginal: List<String>, wordsTranslated: List<String>) {
    realm.executeTransaction { realm ->
      logMethod("thread: ${Thread.currentThread().name}")
      val parentImage = realm.createObject(Image::class.java)
      parentImage.set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
      wordsOriginal.forEachIndexed { index, original ->
        val word = realm.createObject(Word::class.java)
        word.id = System.currentTimeMillis()
        word.original = original
        word.translated = wordsTranslated.get(index)
        parentImage.words.add(word)
      }
    }
    realm.refresh()
  }

  //  push operations:
//  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String)
//      : Observable<Image> {
//    logMethod(level = Log.WARN)
//    return Observable.create<Image>({ emitter ->
//      realm.executeTransaction({ realm ->
//        logMethod(level = Log.WARN)
//        realm.refresh()
//        emitter.onNext(
//            realm.createObject(Image::class.java)
//                .set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
//        )
//        emitter.onComplete()
//      })
//    }).subscribeOn(AndroidSchedulers.mainThread())
//  }
//
//  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
//      wordsOriginal: List<String>): Observable<Image> {
//    logMethod(level = Log.WARN)
//    return Observable.create<Image>({ emitter ->
//      try {
//        var imageParent: Image? = null
//        realm.executeTransaction({ realm ->
//          logMethod(level = Log.WARN)
//          imageParent = realm.createObject(
//              Image::class.java)//todo: replace that with calling on reference 'latestImage'
//          imageParent?.set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
//          wordsOriginal.forEach {
//            val wordOriginal = realm.createObject(Word::class.java)
//            wordOriginal.id = System.currentTimeMillis() //todo: what if user changes its system time?
//            wordOriginal.original = it
//            imageParent?.words?.add(wordOriginal)
//          }
//        })
//
//        realm.executeTransaction({
//          val testObj = realm.createObject(Image::class.java)
//          testObj.model = "blelbelbleeble1231313"
//
//        })
//        realm.refresh()
//        emitter.onNext(imageParent)
//        emitter.onComplete()
//      } catch (exc: Throwable) {
//        emitter.onError(exc)
//      }
//    }).subscribeOn(AndroidSchedulers.mainThread())
//  }
//
//  override fun pushWordsOriginal(wordsOriginal: List<String>): Observable<Image> {
//    logMethod(level = Log.WARN)
//    return Observable.create<Image>({ emitter ->
//      var imageParent: Image? = null
//      realm.executeTransaction({ realm ->
//        logMethod(level = Log.WARN)
//        imageParent = realm.where(Image::class.java).findAllSorted("dateTime",
//            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
//        wordsOriginal.forEach {
//          val wordOriginal = realm.createObject(Word::class.java)
//          wordOriginal.id = System.currentTimeMillis() //todo: what if user changes its system time?
//          wordOriginal.original = it
//          imageParent?.words?.add(wordOriginal)
//        }
//      })
//      realm.refresh()
//      emitter.onNext(imageParent)
//      emitter.onComplete()
//    }).subscribeOn(AndroidSchedulers.mainThread())
//  }
//
//
//  override fun pushWordsTranslated(wordsTranslated: List<String>): Observable<Image> {
//    logMethod(level = Log.WARN)
//    return Observable.create<Image>({ emitter ->
//      realm.executeTransaction({
//        //        logMethod(level = Log.WARN)
//        val imageParent = realm.where(Image::class.java).findAllSorted("dateTime",
//            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
//        imageParent.words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
//        realm.refresh()
//        emitter.onNext(imageParent)
//        emitter.onComplete()
//      })
//    }).subscribeOn(AndroidSchedulers.mainThread())
//  }
//
//
//  override fun fetchAllImages(): Flowable<List<Image>> {
//    return realm.where(Image::class.java).findAllSorted("dateTime",
//        Sort.DESCENDING).asFlowable().share().replay().autoConnect().subscribeOn(
//        AndroidSchedulers.mainThread())
//  }
//
//  override fun fetchLastImage(): Flowable<Image> {
//    return realm.where(Image::class.java).findAllSorted("dateTime",
//        Sort.DESCENDING).asFlowable().map { listImages -> listImages[0] }.skip(1).subscribeOn(
//        AndroidSchedulers.mainThread())
//  }
//
//  override fun fetchAllWords(): Flowable<List<Word>> {
//    return realm.where(Word::class.java).findAllSorted("id",
//        Sort.DESCENDING).asFlowable().subscribeOn(AndroidSchedulers.mainThread())
//  }
}