package com.android.szparag.saymyname.repositories

import com.android.szparag.saymyname.repositories.entities.Image
import com.android.szparag.saymyname.repositories.entities.Word
import com.android.szparag.saymyname.utils.ERROR_REPOSITORY_PUSH_IMAGE_NULL
import com.android.szparag.saymyname.utils.Logger
import com.android.szparag.saymyname.utils.asFlowable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import io.realm.Sort

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class SaymynameImagesWordsRepository : ImagesWordsRepository {

  private val logger = Logger.create(SaymynameImagesWordsRepository::class)
  protected lateinit var realm: Realm
  private var imagesSubscription: Disposable? = null


  override fun attach(): Completable {
    logger.debug("attach")
    return Completable.create({ emitter ->
      logger.debug("attach.Completable.create")
      realm = Realm.getDefaultInstance()
      logRealmChanges(realm)
      emitter.onComplete()
    }).subscribeOn(AndroidSchedulers.mainThread())
  }

  override fun detach(): Completable {
    logger.debug("detach")
    return Completable.fromAction {
      logger.debug("detach.Completable.create")
      imagesSubscription?.dispose()
      realm.close()
    }
  }

  private fun logRealmChanges(realm: Realm) {
    logger.debug("logRealmChanges, realm: $realm")
    realm.addChangeListener { realm ->
      realm.where(Image::class.java).findAll().forEach {
        logger.debug("logRealmChanges.listener, image: $it")
      }
    }
  }

  //todo: shouldnt this be Completable?
  override fun pushImage(imageBase64: ByteArray, languageFrom: String, languageTo: String, model: String,
      wordsOriginal: List<String>, wordsTranslated: List<String>): Observable<Image> {
    logger.debug(
        "pushImage, languageFrom: $languageFrom, languageTo: $languageTo, model: $model, wordsOriginal: $wordsOriginal, wordsTranslated: $wordsTranslated, imageBase64: ${imageBase64.hashCode()}")
    return Observable.create { emitter ->
      logger.debug("pushImage.Observable.create")
      var parentImage: Image? = null
      try {
        realm.executeTransaction { realm ->
          logger.debug("pushImage.Observable.create.executeTransaction, thread: ${Thread.currentThread().name}")
          parentImage = realm.createObject(Image::class.java).apply {
            this.set(System.currentTimeMillis(), imageBase64, languageFrom, languageTo, model)
          }
          wordsOriginal.forEachIndexed { index, original ->
            parentImage?.words?.add(realm.createObject(Word::class.java).apply {
              this.set(System.currentTimeMillis(), original, wordsTranslated[index])
            })
          }
        }
      } catch (exc: Exception) {
        logger.error("pushImage.Observable.executeTransaction.errored", exc)
        emitter.onError(exc)
      }
      parentImage?.let { emitter.onNext(it) } ?: emitter.onError(ERROR_REPOSITORY_PUSH_IMAGE_NULL)
    }
  }

  override fun fetchAllImages(): Flowable<List<Image>> {
    return realm
        .where(Image::class.java)
        .findAllSorted("dateTime", Sort.DESCENDING)
        .asFlowable()
        .subscribeOn(AndroidSchedulers.mainThread())
  }

}