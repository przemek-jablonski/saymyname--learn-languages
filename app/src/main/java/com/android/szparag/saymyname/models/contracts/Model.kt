package com.android.szparag.saymyname.models.contracts

import com.android.szparag.saymyname.presenters.contracts.Presenter
import com.android.szparag.saymyname.repositories.contracts.Repository
import com.android.szparag.saymyname.services.contracts.NetworkService

/**
 * Created by Przemyslaw Jablonski (github.com/sharaquss, pszemek.me) on 7/6/2017.
 */
interface Model {

//  fun attach(presenter: P)
//
//  val presenter : P?
  val service : NetworkService
//  val repository : Repository

}