package com.example.vitrader


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.navigation.TransactionViewPager
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.model.CoinRepository
import com.example.vitrader.utils.viewmodel.*
import java.lang.IllegalArgumentException

class TransactionActivity : ComponentActivity() {
    private lateinit var coinViewModel: CoinViewModel
    private lateinit var orderBookViewModel: OrderBookViewModel
    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeVariables()

        setContent{
            VitraderTheme {
                Surface(color = MaterialTheme.colors.background) {
                    TransactionViewPager(coinViewModel, orderBookViewModel, userAccountViewModel, userProfileViewModel)
                }
            }
        }
    }

    private fun initializeVariables() {
        val bundle = intent.extras
        val symbol = bundle?.getString("symbol")!!

        coinViewModel = ViewModelProvider(this, CoinViewModelFactory(symbol))[CoinViewModel::class.java]
        orderBookViewModel = ViewModelProvider(this, OrderBookViewModelFactory(symbol))[OrderBookViewModel::class.java]
        userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
    }

    override fun onRestart() {
        super.onRestart()
        CoinRepository.launchGettingExternalCoinData()
    }
}
