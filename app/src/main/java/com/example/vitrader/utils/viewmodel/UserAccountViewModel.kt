package com.example.vitrader.utils.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.vitrader.utils.model.UserAccountRepository
import java.math.BigDecimal
import java.text.DecimalFormat

class UserAccountViewModel : ViewModel() {
    private val userAccountRepository = UserAccountRepository
    private val userAccountData get() = userAccountRepository.userAccountData

    val krw get() = userAccountData.value.krw
    val krwStringFormat: String get() = DecimalFormat("#,###").format(BigDecimal(userAccountData.value.krw).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())
    val totalBuy get() = userAccountData.value.totalBuy
    val totalBuyStringFormat: String get() = DecimalFormat("#,###").format(BigDecimal(userAccountData.value.totalBuy).setScale(0, BigDecimal.ROUND_HALF_UP).toLong())

    val possessingCoins get() = userAccountData.value.possessingCoins
    val bookmark get() = userAccountData.value.bookmark


    /** returns user average buy price of symbol(coin) */
    fun getAverage(symbol: String) : BigDecimal {
        val keyIterator = possessingCoins[symbol]?.keys?.iterator()
        var averagePrice = 0.0
        if(keyIterator?.hasNext() == true)
            averagePrice = keyIterator.next().toDouble()
        return BigDecimal(averagePrice).setScale(4, BigDecimal.ROUND_HALF_UP)
    }
    fun isSymbolNull(symbol: String): Boolean = userAccountData.value.possessingCoins[symbol] == null
    fun getCoinCount(symbol: String): BigDecimal =
        BigDecimal(userAccountData.value.possessingCoins[symbol]?.values?.sum() ?: 0.0).setScale(8, BigDecimal.ROUND_HALF_UP)

    @RequiresApi(Build.VERSION_CODES.O)
    fun buy(symbol: String, price: Double, count: Double, onMessage: (String) -> Unit) {

        val toastMessage = if(userAccountData.value.krw >= (price * count).toLong()) {
            if(price * count < 5000.0)
                "최소 매수 금액은 5000krw 입니다"
            else {
                userAccountRepository.buy(symbol, price, count)
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
                userAccountRepository.sell(symbol, price, count)
                "매도가 완료되었습니다"
            }
        }
        else "보유 수량이 부족합니다"

        onMessage(toastMessage)
    }

    fun bookmark(symbol: String?) {
        if (symbol != null) {
            if(userAccountData.value.bookmark.contains(symbol))
                userAccountRepository.removeBookmark(symbol)
            else
                userAccountRepository.addBookmark(symbol)
        }

    }
}