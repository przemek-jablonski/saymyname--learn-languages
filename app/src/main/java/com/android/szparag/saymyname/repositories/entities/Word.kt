package com.android.szparag.saymyname.repositories.entities

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
@RealmClass
open class Word(
    @PrimaryKey var id: Long? = -1,
    var original: String = String(),
    var translated: String = String(),
    var wordOccurences: Int? = -1
) : RealmObject() {

}