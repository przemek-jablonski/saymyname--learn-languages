package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
@RealmClass
open class Image(
    @Index var dateTime: Long = -1,
    var imageBase64: ByteArray? = null,
    var words: RealmList<Word> = RealmList(),
    var languageFrom: Int? = null,
    var languageTo: Int? = null,
    var model: String? = null
) : RealmObject() {

  fun set(dateTime: Long, imageBase64: ByteArray?, languageFrom: Int?, languageTo: Int?, model: String?): Image {
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

}