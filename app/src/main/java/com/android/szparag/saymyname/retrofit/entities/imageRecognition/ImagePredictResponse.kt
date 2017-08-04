package com.android.szparag.saymyname.retrofit.entities.imageRecognition

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.LinkedList

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/1/2017.
 */
data class ImagePredictResponse(@SerializedName("status") @Expose var status: Status? = null,
    @SerializedName("outputs") @Expose var outputs: List<Output>? = null) {
  //todo: that shit's ugly, refactor!
  fun getConcepts() : List<Concept>{
    outputs?.let {
      val combinedConcepts = LinkedList<Concept>()
      for (output in it) {
        combinedConcepts.addAll(output.getConcepts()!!.toList())
      }
      return combinedConcepts
    }
    return emptyList()
  }
}