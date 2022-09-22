package com.example.vitrader.utils.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vitrader.utils.model.UserRepository
import com.example.vitrader.utils.userCoinCount
import java.math.BigDecimal
import java.text.DecimalFormat

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository
    private val userData get() = userRepository.userData

    val krw get() = userData.value.krw
    val krwStringFormat: String get() = DecimalFormat("#,###").format(BigDecimal(userData.value.krw).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())
    val totalBuy get() = userData.value.totalBuy
    val totalBuyStringFormat: String get() = DecimalFormat("#,###").format(BigDecimal(userData.value.totalBuy).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())

    val possessingCoins get() = userData.value.possessingCoins
    val bookmark get() = userData.value.bookmark



    fun getAverage(symbol: String) : BigDecimal {
        val keyIterator = possessingCoins[symbol]?.keys?.iterator()
        var averagePrice = 0.0
        if(keyIterator?.hasNext() == true)
            averagePrice = keyIterator.next().toDouble()
        return BigDecimal(averagePrice).setScale(8, BigDecimal.ROUND_HALF_UP)
    }
    fun isSymbolNull(symbol: String): Boolean = userData.value.possessingCoins[symbol] == null
    fun getCoinCount(symbol: String): BigDecimal =
        BigDecimal(userData.value.possessingCoins[symbol]?.values?.sum() ?: 0.0).setScale(8, BigDecimal.ROUND_HALF_UP)

    fun buy(symbol: String, price: Double, count: Double, onMessage: (String) -> Unit) {

        val toastMessage = if(userData.value.krw >= (price * count).toLong()) {
            if(price * count < 5000.0)
                "최소 매수 금액은 5000krw 입니다"
            else {
                userRepository.buy(symbol, price, count)
                "매수가 완료되었습니다"
            }
        }
        else "보유 krw이 부족합니다"
        // TODO(DB의 KRW 값과 비교)

        onMessage(toastMessage)
    }

    fun sell(symbol: String, price: Double, count: Double, onMessage: (String) -> Unit) {
        val toastMessage = if(getCoinCount(symbol).toDouble() >= count) {
            if(count <= 0.0)
                "알맞은 수량을 입력해주세요"
            else {
                userRepository.sell(symbol, price, count)
                "매도가 완료되었습니다"
            }
        }
        else "보유 수량이 부족합니다"

        onMessage(toastMessage)
    }

    fun bookmark(symbol: String?) {
        if (symbol != null) {
            if(userData.value.bookmark.contains(symbol))
                userRepository.removeBookmark(symbol)
            else
                userRepository.addBookmark(symbol)
        }

    }
}