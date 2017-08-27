package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
@RealmClass
open class Word(
    @Index var id: Long? = -1,
    var original: String = String(),
    var translated: String = String()
) : RealmObject() {

  fun set(id: Long, original: String, translated: String) {
    this.id = id
    this.original = original
    this.translated = translated
  }

  override fun toString(): String {
    return "\t [WORD] [id: $id | original: $original | translated: $translated] (${hashCode()})\n"
  }
}