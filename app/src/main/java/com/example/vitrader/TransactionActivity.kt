package com.example.vitrader


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.vitrader.navigation.TransactionViewPager
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.model.CoinRepository
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.UserViewModel
import com.example.vitrader.utils.viewmodel.CoinViewModelFactory
import java.lang.IllegalArgumentException

class TransactionActivity : ComponentActivity() {
    private lateinit var coinViewModel: CoinViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeVariables()

        setContent{
            VitraderTheme {
                Surface(color = MaterialTheme.colors.background) {
                    TransactionViewPager(coinViewModel, userViewModel)
                }
            }
        }
    }

    private fun initializeVariables() {
        val bundle = intent.extras
        val symbol = bundle?.getString("symbol") ?: { throw IllegalArgumentException("Exception occurred while getting coin symbol") }

        coinViewModel = ViewModelProvider(this, CoinViewModelFactory(symbol as String))[CoinViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onRestart() {
        super.onRestart()
        CoinRepository.launchGettingExternalCoinData()
    }
}
