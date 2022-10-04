package com.example.vitrader.utils.model

import androidx.compose.runtime.mutableStateOf
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.example.vitrader.utils.dbDoubleFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

object UserAccountRepository {

    private const val TAG = "UserRepository"
    const val FEE = 0.0005

    private val userRemoteDataSource = UserRemoteDataSource
    private var _userAccountData = mutableStateOf(UserAccountData())
    val userAccountData get() = _userAccountData

    init {
        setInitialUserData()
    }

    private fun setInitialUserData() {
        CoroutineScope(Dispatchers.Default).launch {
            _userAccountData.value = userRemoteDataSource.getUserAccountData()
        }
    }

    private fun setUserData(newUserAccountData: UserAccountData) {
        _userAccountData.value = newUserAccountData
    }

    fun buy(symbol: String, price: Double, count: Double) {

        val chargedCount = count * (1 - FEE)        // 수수료 부과된 개수
        val newUserData = userAccountData.value.copy()

        // About "possessingCoins"
        if(newUserData.possessingCoins[symbol] == null)
            newUserData.possessingCoins[symbol] = mutableMapOf()

        var averagePrice = price
        var possessCount = chargedCount

        var prevTotalBuyOfThis = 0L

        val keyIterator = newUserData.possessingCoins[symbol]?.keys?.iterator()
        if(keyIterator?.hasNext() == true) {     // 이미 보유 중인 코인이면
            val prevAveragePrice = keyIterator.next()     // 기존 평균가
            val prevCount = newUserData.possessingCoins[symbol]?.get(prevAveragePrice) ?: 0.0      // 기존 보유 개수
            prevTotalBuyOfThis = (prevAveragePrice.toDouble() * prevCount).roundToLong()

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
        newUserData.totalBuy -= prevTotalBuyOfThis
        newUserData.totalBuy += (averagePrice * possessCount).roundToLong()

        update(newUserData)
    }

    fun sell(symbol: String, price: Double, count: Double) {

        val newUserData = userAccountData.value.copy()


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

    private fun update(newUserAccountData: UserAccountData = this._userAccountData.value) {
        setUserData(newUserAccountData)
        userRemoteDataSource.updateAccount(newUserAccountData)
    }

    fun addBookmark(symbol: String) {
        _userAccountData.value.bookmark.add(symbol)
        userRemoteDataSource.updateBookmark(_userAccountData.value.bookmark)
    }
    fun removeBookmark(symbol: String) {
        _userAccountData.value.bookmark.remove(symbol)
        userRemoteDataSource.updateBookmark(_userAccountData.value.bookmark)
    }
}