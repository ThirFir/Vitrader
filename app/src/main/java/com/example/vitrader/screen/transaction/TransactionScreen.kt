package com.example.vitrader.screen.transaction

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.theme.Blue1600
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.SymbolFormat
import com.example.vitrader.utils.model.UserAccountRepository
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import java.lang.IllegalArgumentException
import java.math.BigDecimal


internal enum class TransactionState(val koreaName: String) {
    BUY("매수"), SELL("매도"), HISTORY("거래내역")
}

@Composable
fun TransactionScreen(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(){
            Box(Modifier
                .fillMaxHeight()
                .weight(2f)){
                //TODO("호가")
            }
            Column(Modifier.weight(3f)){
                var selectedTap by remember { mutableStateOf(TransactionState.BUY) }

                //TODO("매수 매도 내역")
                Column {
                    TransactingChangeTab(selected = selectedTap) { selectedTap = it }
                    TransactingScreen(coinViewModel, userAccountViewModel, selectedTap)
                }
                Box(){
                    //TODO("dd")
                }
            }
        }
    }
}

@Composable
internal fun TransactingChangeTab(selected: TransactionState, onChanged: (TransactionState) -> Unit) {
    val selectedTabColor = MaterialTheme.colors.surface
    val unSelectedTabColor = Blue1600

    Row() {
        for(tab in TransactionState.values()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(if (selected == tab) selectedTabColor else unSelectedTabColor)
                    .padding(6.dp)
                    .noRippleClickable { onChanged(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                Text(tab.koreaName)
            }
        }
    }
}

@Composable
internal fun TransactingScreen(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel, selectedTap: TransactionState) {

    val modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)

    val selectedBuyWay = remember { mutableStateListOf(false, true, false) }
    val buyWayList = listOf("지정가", "시장가", "예약")

    val symbol = coinViewModel.coin?.info?.symbol ?: throw IllegalArgumentException("Failed load symbol")


    if(selectedTap == TransactionState.BUY || selectedTap == TransactionState.SELL) {
        Column(modifier = modifier) {
            Column(modifier = Modifier
                .padding()
                .padding(bottom = 12.dp)) {
                Row(Modifier
                    .padding()
                    .padding(bottom = 12.dp)) {
                    for (i in selectedBuyWay.indices) {
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selectedBuyWay[i], onClick = {
                                for (j in selectedBuyWay.indices)
                                    selectedBuyWay[j] = false
                                selectedBuyWay[i] = true
                            })
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(buyWayList[i], fontSize = 14.sp)
                        }
                    }
                }
                Row() {
                    Text("주문가능", modifier = Modifier.weight(1f), fontSize = 12.sp)
                    val textValue =
                        if (selectedTap == TransactionState.BUY)
                            userAccountViewModel.krwStringFormat + " KRW"
                        else {

                            if (userAccountViewModel.isSymbolNull(symbol)) 0.toString() + " " + SymbolFormat.get(
                                coinViewModel.coin?.info?.symbol!!)

                            else userAccountViewModel.getCoinCount(symbol).toPlainString() + " " + SymbolFormat.get(
                                coinViewModel.coin?.info?.symbol!!) + "\n" + "≈" + NumberFormat.krwFormat(
                                coinViewModel.coin!!.ticker.trade_price * userAccountViewModel.getCoinCount(symbol).toDouble()) + " KRW"

                        }
                    Text(textValue, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                }
            }
            if (selectedBuyWay[0]) PreparingScreen()
            else if (selectedBuyWay[1]) ByMarketPriceView(coinViewModel, userAccountViewModel, selectedTap)
            else if (selectedBuyWay[2]) PreparingScreen()
        }
    }
    else {
        PreparingScreen()
    }
}

@Composable
fun PreparingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("준비 중입니다.")
    }
}

@Composable
internal fun ByMarketPriceView(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel, tab: TransactionState) {

    val context = LocalContext.current
    val symbol = coinViewModel.coin?.info?.symbol ?: throw IllegalArgumentException("Can't load symbol")

    if(coinViewModel.coin?.ticker?.trade_price == null)
        throw IllegalArgumentException("exception occurred while getting coin trade_price")

    val textInputGuide = if (tab == TransactionState.BUY) "총액" else "수량"

    Column() {

        var input by remember { mutableStateOf("0") }
        Row(modifier = Modifier.height(26.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Row(modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = textInputGuide,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center)
            }
            BasicTextField(value = input, onValueChange = { inputText ->
                input =
                    if (tab == TransactionState.BUY) inputText.filter { it.isDigit() }
                    else {
                        if (inputText.contains("/[^0-9]/g".toRegex())) return@BasicTextField  // 숫자, . 제외한 입력은 무시
                        if (inputText.contains('-')) return@BasicTextField
                        if (inputText.filter { it == '.' }.length >= 2) return@BasicTextField
                        inputText
                    }
            }, singleLine = true, textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 14.sp,
            ), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primaryVariant)
                    .padding(horizontal = 6.dp)
                    .padding(top = 3.dp),
            )

            var isExpanded by remember { mutableStateOf(false) }
            var currentRatio by remember { mutableStateOf("가능") }
            val ratioList = listOf("최대" to 1.0, "50%" to 0.5, "25%" to 0.25, "10%" to 0.1)

            Column(modifier = Modifier.fillMaxHeight()) {

                Button(onClick = { isExpanded = !isExpanded }, contentPadding = PaddingValues(0.dp), modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp)) {
                    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                        Text(currentRatio,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center)
                    }
                }
                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    for (ratio in ratioList) {
                        DropdownMenuItem(onClick = {
                            currentRatio = ratio.first
                            isExpanded = false

                            input =
                                if (tab == TransactionState.BUY) BigDecimal(userAccountViewModel.krw * ratio.second).setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString()
                                else {
                                    if (userAccountViewModel.getCoinCount(symbol).toDouble() != 0.0) BigDecimal(
                                        userAccountViewModel.getCoinCount(symbol).toDouble() * ratio.second).setScale(8,
                                        BigDecimal.ROUND_HALF_UP).toString() else 0.0.toString()
                                }
                        }) {
                            Text(ratio.first)
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("수수료 : " + NumberFormat.coinRate(UserAccountRepository.FEE), fontSize = 8.sp)
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.SpaceAround) {
            Button(modifier = Modifier.size(width = 100.dp, height = 40.dp),
                onClick = {      // Reset Button
                input = "0"
            }) {
                Text("초기화")
            }

            /**
             *  Buy or Sell
             */
            Button(modifier = Modifier.size(width = 100.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = if (tab == TransactionState.BUY) Color(
                    0xFF28B417) else Color(0xFFAC2525)),
                onClick = {
                if (tab == TransactionState.BUY) {    // BUY
                    userAccountViewModel.buy(symbol = symbol,
                        price = coinViewModel.coin?.ticker?.trade_price!!,
                        count = if (input == "") 0.0 else BigDecimal(BigDecimal(input).setScale(8,
                            BigDecimal.ROUND_HALF_UP)
                            .toDouble() / coinViewModel.coin?.ticker?.trade_price!!).toDouble()) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                } else {          // SELL
                    userAccountViewModel.sell(symbol = symbol,
                        price = coinViewModel.coin?.ticker?.trade_price!!,
                        count = if (input == "") 0.0 else BigDecimal(input).setScale(8,
                            BigDecimal.ROUND_HALF_UP).toDouble()) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(if (tab == TransactionState.BUY) "매수" else "매도")
            }
        }
    }
}



@Composable
fun HistoryView(modifier: Modifier) {

}
