package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
@RealmClass
open class Image(
    var dateTime: Long = -1,
    var imageBase64: ByteArray? = null,
    var words: RealmList<Word> = RealmList(),
    var languageFrom: Int? = null,
    var languageTo: Int? = null,
    var model: String? = null
) : RealmObject() {

  fun set(dateTime: Long, imageBase64: ByteArray?, languageFrom: Int?, languageTo: Int?, model: String?) {
    this.dateTime = dateTime
    this.imageBase64 = imageBase64
    this.languageFrom = languageFrom
    this.languageTo = languageTo
    this.model = model
  }

}