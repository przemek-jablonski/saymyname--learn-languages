package com.android.szparag.saymyname.repositories.entities

import io.realm.annotations.PrimaryKey
import java.util.LinkedList

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 8/5/2017.
 */
open class Image(
    @PrimaryKey var dateTime: Long,
    var imageBase64: String,
    var words: MutableList<Word> = LinkedList(),
    var languageFrom: Int,
    var languageTo: Int,
    var model: Int
) : io.realm.RealmObject() {
}