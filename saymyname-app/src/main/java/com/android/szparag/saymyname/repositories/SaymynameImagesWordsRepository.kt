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
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import java.util.concurrent.TimeUnit.SECONDS

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
      emitter.onComplete()
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun detach() {
    logMethod(level = Log.WARN)
    realm.close()
    imagesSubscription?.dispose()
  }

  override fun logRealmChanges() {
    //todo: refactor to
    logMethod(level = Log.WARN)
    Observable.create<List<Image>> { emitter ->
      try {
        Realm.getDefaultInstance()
            .where(Image::class.java)
            .findAllSorted("dateTime", Sort.DESCENDING)
            .addChangeListener {
              realmResults, changeSet ->
              emitter.onNext(realmResults)
            }
      } catch (exc: Throwable) {
        emitter.onError(exc)
      }
    }.subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .delaySubscription(2, SECONDS)
        .subscribeBy(
            onNext = {
              images ->
              images.forEachIndexed { index, image ->
                Log.d("ImagesWordsRepository",
                    "[Image ($index)]: {${image.dateTime}},{${image.languageFrom}},{${image.languageTo}},{${image.model}}")
                image.words.forEachIndexed { i, word ->
                  Log.d("ImagesWordsRepository", "[Image $index][Word $i]: $word")
                }
              }
            },
            onError = {
              logMethod("logRealmChanges ERRORED")
              logMethod(it.toString(), Log.ERROR)
              it.printStackTrace()
            },
            onComplete = {
              logMethod("logRealmChanges ERRORED")
            }
        )
  }


  //  push operations:
  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String)
      : Observable<Image> {
    logMethod(level = Log.WARN)
    return Observable.create<Image>({ emitter ->
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod(level = Log.WARN)
        emitter.onNext(
            realm.createObject(Image::class.java)
                .set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
        )
      })
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      wordsOriginal: List<String>): Observable<Image> {
    logMethod(level = Log.WARN)
    return Observable.create<Image>({ emitter ->
      try {
        Realm.getDefaultInstance().executeTransaction({ realm ->
          logMethod(level = Log.WARN)
          val imageParent = realm.createObject(
              Image::class.java)//todo: replace that with calling on reference 'latestImage'
          imageParent.set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
          wordsOriginal.forEach {
            val wordOriginal = realm.createObject(Word::class.java)
            wordOriginal.id = System.currentTimeMillis() //todo: what if user changes its system time?
            wordOriginal.original = it
            imageParent.words.add(wordOriginal)
          }
          emitter.onNext(imageParent)
          emitter.onComplete()
        })
      } catch (exc: Throwable) {
        emitter.onError(exc)
      }
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun pushWordsOriginal(wordsOriginal: List<String>): Observable<Image> {
    logMethod(level = Log.WARN)
    return Observable.create<Image>({ emitter ->
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod(level = Log.WARN)
        val imageParent = realm.where(Image::class.java).findAllSorted("dateTime",
            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
        wordsOriginal.forEach {
          val wordOriginal = realm.createObject(Word::class.java)
          wordOriginal.id = System.currentTimeMillis() //todo: what if user changes its system time?
          wordOriginal.original = it
          imageParent.words.add(wordOriginal)
        }
        emitter.onNext(imageParent)
      })
    }).subscribeOn(AndroidSchedulers.mainThread())
  }


  override fun pushWordsTranslated(wordsTranslated: List<String>): Observable<Image> {
    logMethod(level = Log.WARN)
    return Observable.create<Image>({ emitter ->
      realm.executeTransaction({
//        logMethod(level = Log.WARN)
        val imageParent = realm.where(Image::class.java).findAllSorted("dateTime",
            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
        imageParent.words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
        emitter.onNext(imageParent)
      })
    }).subscribeOn(AndroidSchedulers.mainThread())
  }


  override fun fetchAllImages(): Flowable<List<Image>> {
    return realm.where(Image::class.java).findAllSorted("dateTime",
        Sort.DESCENDING).asFlowable().share().replay().autoConnect().subscribeOn(
        AndroidSchedulers.mainThread())
  }

  override fun fetchLastImage(): Flowable<Image> {
    return realm.where(Image::class.java).findAllSorted("dateTime",
        Sort.DESCENDING).asFlowable().map { listImages -> listImages[0] }.skip(1).subscribeOn(
        AndroidSchedulers.mainThread())
  }

  override fun fetchAllWords(): Flowable<List<Word>> {
    return realm.where(Word::class.java).findAllSorted("id",
        Sort.DESCENDING).asFlowable().subscribeOn(AndroidSchedulers.mainThread())
  }
}