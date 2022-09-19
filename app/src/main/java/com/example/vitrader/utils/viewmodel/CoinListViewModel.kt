package com.example.vitrader.utils.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vitrader.utils.model.CoinRepository

class CoinListViewModel : ViewModel() {
    val coins get() = CoinRepository.coins
    val mostActiveCoin get() = CoinRepository.getMostActiveCoin()
    val activeCoins get() = CoinRepository.getActiveCoins()
}