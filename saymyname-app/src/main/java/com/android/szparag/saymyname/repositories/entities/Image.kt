package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.RealmClass

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
//cannot use Kotlin's data class because Realm ¯\_(ツ)\_/¯
@RealmClass open class Image(
    @Index var dateTime: Long = -1,
    var imageBase64: ByteArray? = null,
    var words: RealmList<Word> = RealmList(),
    var languageFrom: String? = null,
    var languageTo: String? = null,
    var model: String? = null
) : RealmObject() {

  fun set(dateTime: Long, imageBase64: ByteArray?, languageFrom: String?, languageTo: String?,
      model: String?): Image {
    this.dateTime = dateTime
    this.imageBase64 = imageBase64
    this.languageFrom = languageFrom
    this.languageTo = languageTo
    this.model = model
    return this
  }

  fun getNonTranslatedWords(): List<String> {
    return words.map { word -> word.original }
  }

  fun getTranslatedWords(): List<String> {
    return words.map { word -> word.translated }
  }

  override fun toString(): String {
    var asString = "[IMAGE] [datetime: $dateTime | lanFrom: $languageFrom | lanTo: $languageTo | model: $model | image: $imageBase64 | words: ${words.size}] (hash: ${hashCode()}) \n"
    words.forEach { word -> asString.plus(word.toString()) }
    return asString
  }
}