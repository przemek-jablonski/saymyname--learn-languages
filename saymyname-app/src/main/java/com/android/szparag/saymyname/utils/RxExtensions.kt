package com.android.szparag.saymyname.utils

import hu.akarnokd.rxjava.interop.RxJavaInterop
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

private val realmOnSuccessStub by lazy { OnSuccess { } }
private val COMPLETABLE_NULL_ERROR_THROWABLE_TEXT = "Completable object is null, pushing error value..."
private val OBSERVABLE_NULL_VALUE_THROWABLE_TEXT = "Observable onNext() argument is null, pushing error value..."
private val REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT= "Realm transaction callback errored (with null exception)"


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
    if (this == null) Completable.error { Throwable(COMPLETABLE_NULL_ERROR_THROWABLE_TEXT) } else this

fun CompletableEmitter.safeOnError(throwable: Throwable?) {
  this.onError(throwable ?: Throwable(REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT))
}


fun Realm.executeTransactionAsyncBy(transaction: Transaction, onSuccess: OnSuccess = OnSuccess {},
    onError: OnError = OnError {}) {
  this.executeTransactionAsync(transaction, onSuccess, onError)
}

//fun Realm.executeTransactionAsyncCompletable(transaction: Transaction) {
//  val transactionCompletable = Completable.unsafeCreate {  }
//  val transactionCallback = object : Realm.Transaction.Callback() {
//    override fun onSuccess() { stream.onComplete() }
//    override fun onError(exc: Exception?) { stream.onError(exc ?: Throwable(REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT)) }
//  }
//  this.executeTransactionAsync(transaction, {}, {})
//}
//
//fun Realm.Transaction.Callback.toCompletable(): Completable {
//  return Completable.create { stream ->
//    object : Realm.Transaction.Callback() {
//      override fun onSuccess() { stream.onComplete() }
//      override fun onError(exc: Exception?) { stream.onError(exc ?: Throwable(REALM_COMPLETABLE_NULL_EXCEPTION_THROWABLE_TEXT)) }
//    }
//  }
//}