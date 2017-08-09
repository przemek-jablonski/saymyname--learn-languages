package com.android.szparag.saymyname.retrofit.services.contracts

import android.support.annotation.CallSuper
import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import com.android.szparag.saymyname.utils.logMethod
import io.reactivex.Observable


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface ImageRecognitionNetworkService : NetworkService {
  fun requestImageProcessing(model: String, image: ByteArray): Observable<List<Concept>>
}