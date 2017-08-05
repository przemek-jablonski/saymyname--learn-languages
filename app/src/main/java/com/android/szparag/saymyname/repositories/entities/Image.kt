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
    @PrimaryKey var dateTime: Long = -1,
    var imageBase64: ByteArray? = null,
    var words: RealmList<Word> = RealmList(),
    var languageFrom: Int? = null,
    var languageTo: Int? = null,
    var model: String? = null
) : RealmObject() {

  override fun toString(): String {
    return super.toString()
  }
}