package com.example.vitrader.screen.main

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.navigation.moveToTransactionActivity
import com.example.vitrader.theme.Blue1800
import com.example.vitrader.theme.Blue200
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import kotlin.math.roundToLong

@Composable
fun ProfileScreen(userAccountViewModel: UserAccountViewModel, coinListViewModel: CoinListViewModel) {
    Column() {
        TotalPossessView(userAccountViewModel, coinListViewModel)
    }
}

@Composable
fun TotalPossessView(userAccountViewModel: UserAccountViewModel, coinListViewModel: CoinListViewModel) {

    val context = LocalContext.current

    var totalEvaluation = 0L      // 총 평가
    val userSymbolIterator = userAccountViewModel.possessingCoins.keys.iterator()
    val symbolList = mutableListOf<String>()
    val isExpanded = remember { mutableStateListOf<Boolean>() }
    var prevSize by remember { mutableStateOf(0) }

    for(symbol in userSymbolIterator) {
        if(symbolList.size < userAccountViewModel.possessingCoins.size) {
            symbolList.add(symbol)
        }
        if(isExpanded.size < symbolList.size)
            isExpanded.add(false)
        totalEvaluation += (userAccountViewModel.getCoinCount(symbol).toDouble()
                * (coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0)).roundToLong()
    }
    if(prevSize != 0 && prevSize != symbolList.size){
        isExpanded.clear()
        for(i in 0..prevSize)
            isExpanded.add(false)
    }
    prevSize = symbolList.size

    var userEvalChange by remember { mutableStateOf("EVEN") }
    userEvalChange = if(totalEvaluation - userAccountViewModel.totalBuy > 0) "RISE" else if(totalEvaluation - userAccountViewModel.totalBuy < 0) "FALL" else "EVEN"

    Box(Modifier.padding(16.dp)) {
        Column() {

            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .weight(2f)) {
                Column(Modifier
                    .fillMaxWidth()
                    .padding(24.dp)) {
                    Text("총 보유자산", modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxSize()
                        .weight(3f)) {
                        Text("₩ " + NumberFormat.krwFormat(totalEvaluation + userAccountViewModel.krw),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White, )
                        Row() {
                            if (userEvalChange == "RISE") Text("+", color = Color.Green)
                            if (userAccountViewModel.totalBuy == 0L)
                                Text("0.00%", color = Color.White)
                            else
                                Text(NumberFormat.coinRate((totalEvaluation - userAccountViewModel.totalBuy).toDouble() / userAccountViewModel.totalBuy.toDouble()),
                                    color = NumberFormat.color(userEvalChange))
                            Spacer(Modifier.width(12.dp))
                            Text("(${NumberFormat.krwFormat(totalEvaluation - userAccountViewModel.totalBuy)})",
                                color = NumberFormat.color(userEvalChange))
                        }
                    }
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            Text("보유 KRW", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text(userAccountViewModel.krwStringFormat, color = Color.White, modifier = Modifier.width(150.dp), textAlign = TextAlign.End)
                        }
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            Text("총 평가", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text(NumberFormat.krwFormat(totalEvaluation), color = Color.White, modifier = Modifier.width(150.dp), textAlign = TextAlign.End)
                        }
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                            Text("총 매수", fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text(userAccountViewModel.totalBuyStringFormat, color = Color.White, modifier = Modifier.width(150.dp), textAlign = TextAlign.End)
                        }
                    }
                }
            }

            Column(Modifier
                .fillMaxSize()
                .weight(3f)) {

                LazyColumn {
                    itemsIndexed(symbolList){ index, symbol ->
                        val coinEval = (coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0) * userAccountViewModel.getCoinCount(symbol).toDouble()     // 코인 현재가 * 보유 개수
                        val userEval = userAccountViewModel.getAverage(symbol).toDouble() * userAccountViewModel.getCoinCount(symbol).toDouble()                // 매수 평균가 * 보유 개수

                        ExpandableCard(isExpanded = isExpanded[index], contentColor = Color.White, modifier = Modifier.fillMaxWidth(), backgroundColor = Blue1800,
                            onClick = { isExpanded[index] = !isExpanded[index] },
                            headerContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(coinListViewModel.coins[symbol]?.info?.name ?: "-", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)        // 코인명

                               Column(horizontalAlignment = Alignment.End){

                                   val itemChange = if(coinEval - userEval > 0.0) "RISE" else if(coinEval - userEval < 0.0) "FALL" else "EVEN"

                                   Text(NumberFormat.krwFormat(coinEval.roundToLong() - userEval.roundToLong()), color = NumberFormat.color(itemChange))
                                   Text(NumberFormat.coinRate((coinEval - userEval) / userEval), color = NumberFormat.color(itemChange))
                                }
                            }
                        }) {    // Detail Content
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)) {

                                var coinChange by remember { mutableStateOf("EVEN") }
                                coinChange = if(coinListViewModel.coins[symbol]!!.ticker.change == "RISE") "RISE" else "FALL"

                                Row() {
                                    Column(Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End) {
                                        Text("현재가", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinPrice(coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0), color = NumberFormat.color(coinChange), maxLines = 1, fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("보유 수량", fontSize = 12.sp, color = Blue200)
                                        Text(userAccountViewModel.getCoinCount(symbol).toPlainString(), maxLines = 1, fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("평가 금액", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.krwFormat(coinEval), maxLines = 1, fontSize = 14.sp)
                                    }
                                    Column(Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End) {
                                        Text("전일 대비", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinRate(coinListViewModel.coins[symbol]?.ticker?.signed_change_rate ?: 0.0), color = NumberFormat.color(coinChange), fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("매수평균가", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinPrice(userAccountViewModel.getAverage(symbol)
                                            .toDouble()), maxLines = 1, fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("매수금액", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.krwFormat(userEval), maxLines = 1, fontSize = 14.sp)
                                    }
                                }
                                Spacer(Modifier.height(20.dp))
                                Row(modifier = Modifier.noRippleClickable {
                                    moveToTransactionActivity(context, symbol)
                                }) {
                                    Text("See Coin")
                                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
                                }
                            }
                        }


                    }

                }
            }
        }
    }
}

@Composable
fun PossessList() {

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableCard(
    isExpanded: Boolean,
    headerContent: @Composable () -> Unit, // Header
    contentColor: Color, // Color
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    strokeColor: Color = contentColor,
    onClick: () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    //var expand by remember { mutableStateOf(false) } // Expand State
    val rotationState by animateFloatAsState(if (isExpanded) 180f else 0f) // Rotation State
    var stroke by remember { mutableStateOf(1) } // Stroke State
    Card(
        modifier = modifier
            .animateContentSize( // Animation
                animationSpec = tween(
                    durationMillis = 400, // Animation Speed
                    easing = LinearOutSlowInEasing // Animation Type
                )
            )
            .padding(8.dp),
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(8.dp), // Shape
        border = BorderStroke(stroke.dp, strokeColor), // Stroke Width and Color
        onClick = {
            //expand = !expand
            //stroke = if (expand) 2 else 1
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Control the header Alignment over here.
            ) {
                Box(Modifier.weight(1f)) {
                    headerContent()
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = contentColor, // Icon Color
                    contentDescription = "Drop Down Arrow",
                    modifier = Modifier
                        .rotate(rotationState)
                        .weight(.1f)
                )
            }
            if (isExpanded) {

                Box() {
                    detailContent()
                }
            }
        }
    }

}