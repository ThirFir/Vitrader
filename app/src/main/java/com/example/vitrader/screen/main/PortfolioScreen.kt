package com.example.vitrader.screen.main

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.navigation.moveToTransactionActivity
import com.example.vitrader.theme.Blue1200
import com.example.vitrader.theme.Blue1800
import com.example.vitrader.theme.Blue200
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserViewModel
import kotlin.math.roundToLong

@Composable
fun ProfileScreen(userViewModel: UserViewModel, coinListViewModel: CoinListViewModel) {
    Column() {
        TotalPossessView(userViewModel, coinListViewModel)
    }
}

@Composable
fun TotalPossessView(userViewModel: UserViewModel, coinListViewModel: CoinListViewModel) {

    val context = LocalContext.current

    var totalPossess = 0.0
    val userSymbolIterator = userViewModel.possessingCoins.keys.iterator()
    val symbolList = mutableListOf<String>()
    for(symbol in userSymbolIterator) {
        totalPossess += (userViewModel.getCoinCount(symbol)
            .toDouble() * (coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0))
        symbolList.add(symbol)
    }

    var change by remember { mutableStateOf("EVEN") }
    change = if(totalPossess.roundToLong() - userViewModel.totalBuy > 0) "RISE" else if(totalPossess.roundToLong() - userViewModel.totalBuy < 0) "FALL" else "EVEN"


    Box(Modifier.padding(16.dp)) {
        Column() {

            Box(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .weight(1f)) {
                Column(Modifier
                    .fillMaxWidth()
                    .padding(24.dp)) {
                    Text("총 보유자산", modifier = Modifier.weight(2f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxSize()
                        .weight(3f)) {
                        Text("₩ " + NumberFormat.krwFormat(totalPossess + userViewModel.krw),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f),
                            color = Color.White, )
                        Row(modifier = Modifier.weight(1f)) {
                            if (change == "RISE") Text("+", color = Color.Green)
                            if (userViewModel.totalBuy == 0L)
                                Text("0.00%", color = Color.White)
                            else
                                Text(NumberFormat.coinRate((totalPossess.roundToLong() - userViewModel.totalBuy).toDouble() / userViewModel.totalBuy.toDouble()),
                                    color = NumberFormat.color(change))
                            Spacer(Modifier.width(12.dp))
                            Text("(${NumberFormat.krwFormat(totalPossess - userViewModel.totalBuy)})",
                                color = NumberFormat.color(change))
                        }
                    }
                }
            }

            Column(Modifier
                .fillMaxSize()
                .weight(3f)) {

                val expanded = remember { mutableStateListOf<Boolean>() }
                for (i in symbolList.indices)
                    expanded.add(false)

                LazyColumn {
                    itemsIndexed(symbolList){ index, symbol ->
                        val coinEval = (coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0) * userViewModel.getCoinCount(symbol).toDouble()
                        val userEval = userViewModel.getAverage(symbol).toDouble() * userViewModel.getCoinCount(symbol).toDouble()

                        ExpandableCard(contentColor = Color.White, modifier = Modifier.fillMaxWidth(), backgroundColor = Blue1800, headerContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(coinListViewModel.coins[symbol]?.info?.name ?: "-", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)        // 코인명

                                //보유수량 * 현재가 - 매수총금액액
                               Column(horizontalAlignment = Alignment.End){

                                   val itemChange = if(coinEval - userEval > 0.0) "RISE" else if(coinEval - userEval < 0.0) "FALL" else "EVEN"

                                   Text(NumberFormat.krwFormat(coinEval - userEval), color = NumberFormat.color(itemChange))
                                   Text(NumberFormat.coinRate((coinEval - userEval) / userEval), color = NumberFormat.color(itemChange))
                                }
                            }
                        }) {    // Detail Content
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                Row() {
                                    Column(Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End) {
                                        Text("현재가", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinPrice(coinListViewModel.coins[symbol]?.ticker?.trade_price ?: 0.0), color = NumberFormat.color(change), maxLines = 1, fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("보유 수량", fontSize = 12.sp, color = Blue200)
                                        Text(userViewModel.getCoinCount(symbol).toPlainString(), maxLines = 1, fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("평가 금액", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.krwFormat(coinEval), maxLines = 1, fontSize = 14.sp)
                                    }
                                    Column(Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End) {
                                        Text("전일 대비", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinRate(coinListViewModel.coins[symbol]?.ticker?.signed_change_rate ?: 0.0), color = NumberFormat.color(change), fontSize = 14.sp)
                                        Spacer(Modifier.height(10.dp))

                                        Text("매수평균가", fontSize = 12.sp, color = Blue200)
                                        Text(NumberFormat.coinPrice(userViewModel.getAverage(symbol)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableCard(
    headerContent: @Composable () -> Unit, // Header
    contentColor: Color, // Color
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    strokeColor: Color = contentColor,
    detailContent: @Composable () -> Unit
) {
    var expand by remember { mutableStateOf(false) } // Expand State
    val rotationState by animateFloatAsState(if (expand) 180f else 0f) // Rotation State
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
            expand = !expand
            stroke = if (expand) 2 else 1
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
                    modifier = Modifier.rotate(rotationState)
                        .weight(.1f)
                )
            }
            if (expand) {

                Box() {
                    detailContent()
                }
            }
        }
    }

}