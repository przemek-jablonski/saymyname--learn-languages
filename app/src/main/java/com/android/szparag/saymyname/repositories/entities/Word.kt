package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class Word(
    @PrimaryKey var id: Long,
    var original: String,
    var translated: String?,
    var wordOccurences: Int?
) : RealmObject() {

}