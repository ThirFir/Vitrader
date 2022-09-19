package com.example.vitrader.utils.model

import android.util.Log
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
            Log.d(TAG, userData.value.toString())
        }
    }

    fun buy(symbol: String, price: Double, count: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.buy(symbol, price, count)
            Log.d(TAG, userData.value.toString())
        }
    }

    fun sell(symbol: String, price: Double, count: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.sell(symbol, price, count)
            Log.d(TAG, userData.value.toString())
        }
    }

    fun update(updateUserData: UserData = this._userData.value) {
        this._userData.value = updateUserData
        userRemoteDataSource.update(updateUserData)
    }
}