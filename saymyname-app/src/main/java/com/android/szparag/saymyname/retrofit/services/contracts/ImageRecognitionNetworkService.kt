package com.android.szparag.saymyname.retrofit.services.contracts

import com.android.szparag.saymyname.retrofit.entities.imageRecognition.Concept
import io.reactivex.Observable


/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/5/2017.
 */
interface ImageRecognitionNetworkService : NetworkService {

  enum class ImageRecognitionModel(val modelString: String, val modelId: String) {
    GENERAL("General", "aaa03c23b3724a16a56b629203edc62c"),
    COLOURS("Colours", "eeed0b6733a644cea07cf4c60f87ebb7"),
    FOOD("Food", "bd367be194cf45149e75f01d59f77ba7"),
    TRAVEL("Travel", "eee28c313d69466f836ab83287a54ed9"),
    CLOTHING("Clothing", "e0be3b9d6a454f0493ac3a30784001ff")
  }

  fun requestImageProcessing(modelId: String, image: ByteArray): Observable<List<Concept>>
}