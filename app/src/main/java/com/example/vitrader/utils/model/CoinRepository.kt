package com.example.vitrader.utils.model

import androidx.compose.runtime.mutableStateMapOf
import com.example.vitrader.utils.db.UpbitWebSocketListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CoinRepository {

    private var _coins = mutableStateMapOf<String, Coin>()
    val coins get() = _coins

    init {
        launchGettingExternalCoinData()
    }

    fun addCoin(coin: Coin) {
        _coins[coin.info.symbol] = coin

    }

    fun updateTicker(newTicker: Coin.Ticker) {
        _coins[newTicker.market] = Coin(_coins[newTicker.market]?.info!!, newTicker)
    }

    fun launchGettingExternalCoinData() {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Default).launch {
                UpbitWebSocketListener.launchGettingExternalCoinData()
            }
        }
    }

    fun getMostActiveCoin(): Coin {

        return _coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }[0]
    }

    fun getActiveCoins(): List<Coin> {

        val list = _coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }


        return listOf(list[1], list[2], list[3], list[4])
    }


}