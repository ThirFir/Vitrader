package com.example.vitrader.screen.transaction

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.R
import com.example.vitrader.theme.Blue1200
import com.example.vitrader.theme.Blue1600
import com.example.vitrader.utils.*
import com.example.vitrader.utils.db.HistoryDatabase
import com.example.vitrader.utils.model.History
import com.example.vitrader.utils.model.UserAccountRepository
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.OrderBookViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal enum class TransactionState(val koreaName: String) {
    BUY("매수"), SELL("매도"), HISTORY("거래내역")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(coinViewModel: CoinViewModel, orderBookViewModel: OrderBookViewModel, userAccountViewModel: UserAccountViewModel) {

    val symbol = coinViewModel.coin!!.info.symbol

    val asks = remember { mutableStateMapOf<Double, Double>() }
    val bids = remember { mutableStateMapOf<Double, Double>() }

    var maxSize by remember { mutableStateOf(Double.MIN_VALUE) }

    orderBookViewModel.orderBook!!.units.forEachIndexed { index, unit ->
        if(index == 0) {
            asks.clear()
            bids.clear()
        }
        val askSize = unit["ask_size"]!!
        val bidSize = unit["bid_size"]!!
        asks[unit["ask_price"]!!] = askSize  // 매도 호가
        bids[unit["bid_price"]!!] = bidSize  // 매수 호가

        if(askSize > maxSize)
            maxSize = askSize
        if(bidSize > maxSize)
            maxSize = bidSize
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Row(){
            Box(Modifier
                .fillMaxHeight()
                .weight(2.3f)){

                val scrollState = rememberScrollState()
                Column(modifier = Modifier
                    .verticalScroll(scrollState)) {
                    for (ask in asks.toSortedMap(Collections.reverseOrder())) {
                        val barSize = (ask.value / maxSize).toFloat()
                        val spaceSize = 1f - barSize + Float.MIN_VALUE
                        Row(modifier = Modifier.height(36.dp)) {
                            if (ask.key != 0.toDouble()) {
                                Box(modifier = Modifier.fillMaxHeight().weight(3f).background(color = Color(
                                    0xFFE2CBCB)).padding(horizontal = 2.dp),
                                    contentAlignment = Alignment.CenterEnd) {
                                    AutoResizeText(text = NumberFormat.coinPrice(ask.key), textAlign = TextAlign.End, color = if(ask.key > coinViewModel.coin!!.ticker.prev_closing_price) Color.Green else Color.Red)
                                }
                                Spacer(Modifier.fillMaxHeight().width(1.dp).background(Blue1200))
                                Box(modifier = Modifier.fillMaxHeight().weight(2f).background(color = Color(
                                    0xFFE2CBCB)),
                                    contentAlignment = Alignment.CenterStart) {
                                    Row(modifier = Modifier.height(20.dp).fillMaxWidth()) {
                                        Box(modifier = Modifier.fillMaxHeight().weight(barSize).background(color = Color(
                                            0xFFD17F7F)))

                                        Spacer(Modifier.fillMaxHeight().weight(spaceSize))
                                    }
                                    AutoResizeText(text = BigDecimal(ask.value).setScale(3,
                                        RoundingMode.HALF_UP).toPlainString(), modifier = Modifier.padding().padding(2.dp))
                                }
                            }
                        }
                        Spacer(Modifier.fillMaxWidth().height(1.dp).background(Blue1200))
                    }
                    for (bid in bids.toSortedMap(Collections.reverseOrder())) {
                        val barSize = (bid.value / maxSize).toFloat()
                        val spaceSize = 1f - barSize + Float.MIN_VALUE
                        Row(modifier = Modifier.height(36.dp)) {
                            if (bid.key != 0.toDouble()) {
                                Box(modifier = Modifier.fillMaxHeight().weight(3f).background(color = Color(
                                    0xFFD2E7CF)).padding(horizontal = 2.dp),
                                    contentAlignment = Alignment.CenterEnd) {
                                    AutoResizeText(text = NumberFormat.coinPrice(bid.key), textAlign = TextAlign.End, color = if(bid.key > coinViewModel.coin!!.ticker.prev_closing_price) Color.Green else Color.Red)
                                }
                                Spacer(Modifier.fillMaxHeight().width(1.dp).background(Blue1200))
                                Box(modifier = Modifier.fillMaxHeight().weight(2f).background(color = Color(
                                    0xFFD2E7CF)),
                                    contentAlignment = Alignment.CenterStart) {
                                    Row(modifier = Modifier.height(20.dp).fillMaxWidth()) {
                                        Box(modifier = Modifier.fillMaxHeight().weight(barSize).background(color = Color(
                                            0xFF65D358)))

                                        Spacer(Modifier.fillMaxHeight().weight(spaceSize))
                                    }
                                    AutoResizeText(text = BigDecimal(bid.value).setScale(3,
                                        RoundingMode.HALF_UP).toPlainString(), modifier = Modifier.padding().padding(2.dp))
                                }
                            }
                        }
                        Spacer(Modifier.fillMaxWidth().height(1.dp).background(Blue1200))
                    }
                }
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

@RequiresApi(Build.VERSION_CODES.O)
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
                    AutoResizeText(textValue, textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold), textAlign = TextAlign.End)
                }
            }
            if (selectedBuyWay[0]) PreparingScreen()
            else if (selectedBuyWay[1]) ByMarketPriceView(coinViewModel, userAccountViewModel, selectedTap)
            else if (selectedBuyWay[2]) PreparingScreen()
        }
    }
    else {      // 거래내역
        HistoryView(symbol)
    }
}

@Composable
fun PreparingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("준비 중입니다.")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
                    val price = coinViewModel.coin?.ticker?.trade_price!!
                    val transaction: String
                    val count: Double
                    if (tab == TransactionState.BUY) {    // BUY
                        transaction = "BUY"
                        count =
                            if (input == "") 0.0 else BigDecimal(BigDecimal(input).setScale(8,
                                BigDecimal.ROUND_HALF_UP)
                                .toDouble() / coinViewModel.coin?.ticker?.trade_price!!).toDouble()
                        userAccountViewModel.buy(symbol = symbol, price = price, count = count) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }

                    } else {          // SELL
                        transaction = "SELL"
                        count = if (input == "") 0.0 else BigDecimal(input).setScale(8,
                            BigDecimal.ROUND_HALF_UP).toDouble()
                        userAccountViewModel.sell(symbol = symbol, price = price, count = count) {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                    /** Insert History */
                    HistoryManager.apply {
                        addHistory(makeHistory(symbol, price, count * (1 - UserAccountRepository.FEE), transaction), context)
                    }
                }) {
                Text(if (tab == TransactionState.BUY) "매수" else "매도")
            }
        }
    }
}



@Composable
fun HistoryView(symbol: String) {

    val symbolHistories = HistoryManager.getSymbolHistories(symbol)

    Column {
        LazyColumn() {
            items(symbolHistories) {
                HistoryItem(it)
            }
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    val transactionText: String
    val transactionColor: Color

    if(history.transaction == "BUY") {
        transactionText = "매수"
        transactionColor = Color.Green
    }
    else{
        transactionText = "매도"
        transactionColor = Color.Red
    }
    Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) {
        val data = listOf("시간" to history.date, "가격" to NumberFormat.coinPrice(history.price), "수량" to BigDecimal(history.count).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString(), "총액" to NumberFormat.krwFormat(history.price * history.count))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(SymbolFormat.get(history.symbol), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(transactionText, color = transactionColor, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(2.dp))
        Spacer(Modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(color = Color.Black)
        )
        Spacer(Modifier.height(4.dp))
        for(d in data)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(d.first, modifier = Modifier.weight(1f), fontSize = 14.sp)

                val fontSize = if(d.second.length > 18) 12.sp else 14.sp
                Text(d.second, fontSize = fontSize)
            }
    }
}
