package com.android.szparag.saymyname.retrofit.entities.persistence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class Word(
    @PrimaryKey val id: Long,
    val original: String,
    var translated: String?,
    val languageFrom: Int,
    val languageTo: Int,
    val model: Int,
    var wordOccurences: Int
) : RealmObject() {

}