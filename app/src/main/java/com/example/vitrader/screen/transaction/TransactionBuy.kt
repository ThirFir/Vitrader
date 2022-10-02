package com.example.vitrader.screen.transaction

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import java.lang.IllegalArgumentException


// 지정가 매수 - 시장가가 지정가보다 낮을 경우 바로 매수됨
@Composable
fun BuyByFixedPriceView(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel) {

    val context = LocalContext.current
    val symbol = coinViewModel.coin?.info?.symbol
    if(coinViewModel.coin?.ticker?.trade_price == null)
        throw IllegalArgumentException("exception occurred while getting coin trade_price")

    Column() {

        var count = 0.0
        var krwInput by remember { mutableStateOf("0") }
        var countInput by remember { mutableStateOf("1") }
        Row() {

            Text("총액")
            TextField(value = krwInput, onValueChange = { inputText ->
                krwInput = inputText.filter { it.isDigit() }

                countInput = if(krwInput == "") 0.toString() else (krwInput.toDouble() / coinViewModel.coin?.ticker?.trade_price!!).toString()
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            count = if(krwInput == "") 0.0 else (krwInput.toDouble() / coinViewModel.coin?.ticker?.trade_price!!)

        }
        Row() {

            Text("개수")
            TextField(value = countInput, onValueChange = { inputText ->
                if(inputText.contains("/[^0-9]/g".toRegex())) return@TextField  // 숫자, . 제외한 입력은 무시
                if(inputText.contains('-')) return@TextField
                if(inputText.filter { it == '.' }.length >= 2) return@TextField
                countInput = inputText

                krwInput = if(countInput == "") 0.toString() else String.format("%.2f", coinViewModel.coin?.ticker?.trade_price!! * countInput.toDouble())
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            count = if(countInput == "") 0.0 else countInput.toDouble()
        }

        Button(onClick = {
            userAccountViewModel.buy(symbol = symbol!!, price = coinViewModel.coin?.ticker?.trade_price!!, count = count) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("매수")
        }
    }
}

@Composable
fun BuyByReserveView(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel) {

}