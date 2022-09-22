package com.example.vitrader.utils.model

import androidx.compose.runtime.mutableStateOf
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.example.vitrader.utils.dbDoubleFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

object UserRepository {

    private const val TAG = "UserRepository"
    const val FEE = 0.0005

    private val userRemoteDataSource = UserRemoteDataSource
    private var _userData = mutableStateOf(UserData())
    val userData get() = _userData

    init {
        setInitialUserData()
    }

    private fun setInitialUserData() {
        CoroutineScope(Dispatchers.Default).launch {
            _userData.value = userRemoteDataSource.getUserDataFromDB()
        }
    }

    private fun setUserData(newUserData: UserData) {
        _userData.value = newUserData
    }

    fun buy(symbol: String, price: Double, count: Double) {

        val chargedCount = count * (1 - FEE)
        val newUserData = userData.value.copy()

        // About "possessingCoins"
        if(newUserData.possessingCoins[symbol] == null)
            newUserData.possessingCoins[symbol] = mutableMapOf()

        var averagePrice = price
        var possessCount = chargedCount

        val keyIterator = newUserData.possessingCoins[symbol]?.keys?.iterator()
        if(keyIterator?.hasNext() == true) {     // 이미 보유 중인 코인이면
            val prevAveragePrice = keyIterator.next()     // 기존 평균가
            val prevCount = newUserData.possessingCoins[symbol]?.get(prevAveragePrice) ?: 0.0      // 기존 보유 개수

            possessCount = prevCount + chargedCount

            averagePrice = (prevAveragePrice.toDouble() * prevCount + price * chargedCount) / possessCount     // 새 평균가

            newUserData.possessingCoins[symbol]?.remove(prevAveragePrice)
        }
        newUserData.possessingCoins[symbol]?.set(dbDoubleFormat(averagePrice).toString(), String.format("%.8f", possessCount).toDouble())      // 업데이트된 Map(평균가, 개수)

        val coinEvaluation = (price * count).roundToLong()
        // About "krw"
        newUserData.krw -= coinEvaluation
        if(newUserData.krw <= 1) newUserData.krw = 0

        // About "totalBuy"
        newUserData.totalBuy += (price * chargedCount).roundToLong()

        update(newUserData)
    }

    fun sell(symbol: String, price: Double, count: Double) {

        val newUserData = userData.value.copy()


        val keyIterator = newUserData.possessingCoins[symbol]?.keys?.iterator()
        if(keyIterator?.hasNext() == true) {
            val averagePrice = keyIterator.next()

            newUserData.krw += (price * (count * (1 - FEE))).roundToLong()
            newUserData.totalBuy -= (averagePrice.toDouble() * count).roundToLong()
            if(newUserData.totalBuy < 0) newUserData.totalBuy = 0
            newUserData.possessingCoins[symbol]?.set(averagePrice,
                newUserData.possessingCoins[symbol]?.get(averagePrice)!! - count)

            if(newUserData.possessingCoins[symbol]?.get(averagePrice)!! < 0.00000001)
                newUserData.possessingCoins.remove(symbol)
        }

        update(newUserData)
    }

    private fun update(newUserData: UserData = this._userData.value) {
        setUserData(newUserData)
        userRemoteDataSource.update(newUserData)
    }

    fun addBookmark(symbol: String) {
        _userData.value.bookmark.add(symbol)
        userRemoteDataSource.updateBookmark(_userData.value.bookmark)
    }
    fun removeBookmark(symbol: String) {
        _userData.value.bookmark.remove(symbol)
        userRemoteDataSource.updateBookmark(_userData.value.bookmark)
    }
}