package com.example.vitrader.utils.model

import androidx.compose.runtime.mutableStateOf
import com.example.vitrader.utils.db.UserRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UserRepository {

    private const val TAG = "UserRepository"

    private val userRemoteDataSource = UserRemoteDataSource
    private var _userData = mutableStateOf(UserData())
    val userData get() = _userData

    init {
        setUserData()
    }

    fun setUserData() {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.getUserDataFromDB()
        }
    }

    fun buy(symbol: String, price: Double, count: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.buy(symbol, price, count)
        }
    }

    fun sell(symbol: String, price: Double, count: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.sell(symbol, price, count)
        }
    }

    fun update(updateUserData: UserData = this._userData.value) {
        this._userData.value = updateUserData
        userRemoteDataSource.update(updateUserData)
    }

    fun addBookmark(symbol: String) {
        _userData.value.bookmark.add(symbol)
        CoroutineScope(Dispatchers.Default).launch {
            userRemoteDataSource.updateBookmark(_userData.value.bookmark)
        }
    }
    fun removeBookmark(symbol: String) {
        _userData.value.bookmark.remove(symbol)
        CoroutineScope(Dispatchers.Default).launch {
            userRemoteDataSource.updateBookmark(_userData.value.bookmark)
        }
    }
}