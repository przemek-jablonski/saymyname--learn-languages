package com.android.szparag.saymyname.repositories

import android.util.Log
import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.asFlowable
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {


  protected lateinit var realm: Realm  //todo: change getDefaultInstance() //todo: make Realm operations asynchronous
  private var imagesSubscription: Disposable? = null

  //  lifecycle:
  override fun attach(): Completable {
    return Completable.create ({ emitter ->
      logMethod()
      realm = Realm.getDefaultInstance()
      logRealmChanges()
      emitter.onComplete()
    }).subscribeOn(Schedulers.single())
  }

  override fun detach() {
    logMethod()
    realm.close()
    imagesSubscription?.dispose()
  }

  protected fun logRealmChanges() {
    //todo: refactor to
    logMethod()
//    realm.where(Image::class.java).findAllSorted("dateTime", Sort.DESCENDING).addChangeListener {
//      realmResults, changeSet ->
//      realmResults.forEachIndexed {
//        index, image ->
//        Log.d("ImagesWordsRepository",
//            "[Image ($index)]: {${image.dateTime}},{${image.languageFrom}},{${image.languageTo}},{${image.model}}")
//        image.words.forEachIndexed { i, word ->
//          Log.d("ImagesWordsRepository", "[Image $index][Word $i]: $word")
//        }
//      }
//    }
  }


  //  push operations:
  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String)
      : Observable<Image> {
    logMethod()
    return Observable.create<Image> ({ emitter ->
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod()
        emitter.onNext(
            realm.createObject(Image::class.java)
                .set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
        )
      })
    }).subscribeOn(Schedulers.single())
  }

  override fun pushImage(imageBase64: ByteArray, languageFrom: Int, languageTo: Int, model: String,
      wordsOriginal: List<String>): Observable<Image> {
    logMethod()
    return Observable.create<Image> ({ emitter ->
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod()
        val imageParent = realm.createObject(Image::class.java)//todo: replace that with calling on reference 'latestImage'
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
    }).subscribeOn(Schedulers.single())
  }

  override fun pushWordsOriginal(wordsOriginal: List<String>): Observable<Image> {
    logMethod()
    return Observable.create<Image> ({ emitter ->
      Realm.getDefaultInstance().executeTransaction({ realm ->
        logMethod()
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
    }).subscribeOn(Schedulers.single())
  }


  override fun pushWordsTranslated(wordsTranslated: List<String>): Observable<Image> {
    logMethod()
    return Observable.create<Image> ({ emitter ->
      realm.executeTransaction({
        logMethod()
        val imageParent = realm.where(Image::class.java).findAllSorted("dateTime",
            Sort.DESCENDING).first() //todo: replace that with calling on reference 'latestImage'
        imageParent.words.forEachIndexed { i, word -> word.translated = wordsTranslated[i] }
        emitter.onNext(imageParent)
      })
    }).subscribeOn(Schedulers.single())
  }


  override fun fetchAllImages(): Flowable<List<Image>> {
    return realm.where(Image::class.java).findAllSorted("dateTime",
        Sort.DESCENDING).asFlowable().share().replay().autoConnect().subscribeOn(Schedulers.single())
  }

  override fun fetchLastImage(): Flowable<Image> {
     return realm.where(Image::class.java).findAllSorted("dateTime",
        Sort.DESCENDING).asFlowable().map { listImages -> listImages[0] }.skip(1).subscribeOn(Schedulers.single())
  }

  override fun fetchAllWords(): Flowable<List<Word>> {
    return realm.where(Word::class.java).findAllSorted("id", Sort.DESCENDING).asFlowable().subscribeOn(Schedulers.single())
  }
}