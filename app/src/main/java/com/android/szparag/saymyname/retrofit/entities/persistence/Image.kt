package com.android.szparag.saymyname.retrofit.entities.persistence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class Image(
    @PrimaryKey val dateTime: Long,
    val imageBase64: String,
    var words: List<Word>
) : RealmObject() {
}