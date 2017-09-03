package com.android.szparag.saymyname.utils

import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.OnErrorNotImplementedException
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Realm.Transaction
import io.realm.Realm.Transaction.OnError
import io.realm.Realm.Transaction.OnSuccess
import io.realm.RealmModel
import io.realm.RealmResults
import java.lang.Exception

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 07/08/2017.
 */

private val onNextStub: (Any) -> Unit = {}
private val onErrorStub: (Throwable) -> Unit = {

  throw RuntimeException(it)
}
private val onCompleteStub: () -> Unit = {}

private val realmOnSuccessStub by lazy { OnSuccess { } }
private val COMPLETABLE_NULL_ERROR_THROWABLE_TEXT = "Completable object is null, pushing error value..."
private val OBSERVABLE_NULL_VALUE_THROWABLE_TEXT = "Observable onNext() argument is null, pushing error value..."
private val REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT = "Realm transaction callback errored (with null exception)"
private val DISPOSABLE_ALREADY_IN_CONTAINER = "Disposable is already in given container. Throwing exception"
private val DISPOSABLE_CONTAINER_IS_NULL = "Disposable container is null. Throwing exception"
val DISPOSABLE_ALREADY_IN_CONTAINER_THROWABLE: Throwable by lazy {
  Throwable(DISPOSABLE_ALREADY_IN_CONTAINER)
}
val DISPOSABLE_CONTAINER_NULL_THROWABLE: Throwable by lazy {
  Throwable(DISPOSABLE_CONTAINER_NULL_THROWABLE)
}

fun CompositeDisposable.add(disposable: Disposable?): Boolean {
  disposable?.let { this.add(disposable); return true }
  return false
}

fun <E : RealmModel> RealmResults<E>.asFlowable(): Flowable<List<E>> {
  return RxJavaInterop.toV2Flowable(
      this.asObservable()).map { realmResults -> realmResults.toList() }
}

/**
 * Extension function that pushes error value from Completable object into stream,
 * even if the object itself is completely nulled out in the moment of execution.
 */
fun Completable?.nonNull(): Completable =
    if (this == null) Completable.error {
      Throwable(COMPLETABLE_NULL_ERROR_THROWABLE_TEXT)
    } else this

fun CompletableEmitter.safeOnError(throwable: Throwable?) {
  this.onError(throwable ?: Throwable(REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT))
}


fun Realm.executeTransactionAsyncBy(transaction: Transaction, onSuccess: OnSuccess = OnSuccess {},
    onError: OnError = OnError {}) {
  this.executeTransactionAsync(transaction, onSuccess, onError)
}

//fun <T : Any> Observable<T>.subscribe(
//    onNext: (T) -> Unit = onNextStub,
//    onError: (Throwable) -> Unit = onErrorStub,
//    onComplete: () -> Unit = onCompleteStub
//): Disposable = subscribe(onNext, onError, onComplete)
//
//
//fun Completable.subscribe(
//    onError: (Throwable) -> Unit = onErrorStub,
//    onComplete: () -> Unit = onCompleteStub
//): Disposable {
//  this.cl
//}
//
//
//fun <T : Any> Flowable<T>.subscribe(
//    onNext: (T) -> Unit = onNextStub,
//    onError: (Throwable) -> Unit = onErrorStub,
//    onComplete: () -> Unit = onCompleteStub
//): Disposable = subscribe(onNext, onError, onComplete)


fun Completable.ui(): Completable {
  return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.ui(): Observable<T> {
  return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.ui(): Flowable<T> {
  return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun Completable.single(): Completable {
  return this.subscribeOn(Schedulers.single())
}

fun <T> Observable<T>.single(): Observable<T> {
  return this.subscribeOn(Schedulers.single())
}

fun <T> Flowable<T>.single(): Flowable<T> {
  return this.subscribeOn(Schedulers.single())
}