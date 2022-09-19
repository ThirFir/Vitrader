package com.example.vitrader.screen.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.theme.Blue1200
import com.example.vitrader.utils.*
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import java.util.*

enum class SortState {
    DEFAULT,
    ASCENDING_PRICE,
    DESCENDING_PRICE,
    ASCENDING_RATE,
    DESCENDING_RATE,
    ASCENDING_VOLUME,
    DESCENDING_VOLUME
}

@Composable
fun CoinListScreen(coinListViewModel: CoinListViewModel, onCoinClicked: (String) -> Unit) {
    Column(modifier = Modifier
        .padding(0.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoinSearchBar(coinListViewModel)

        var sortState by remember { mutableStateOf(SortState.DEFAULT) }

        CoinSortBar(coinListViewModel, sortState) { sortState = it }
        AllCoinsListView(coinListViewModel, sortState, onCoinClicked)
    }
}

@Composable
fun CoinSearchBar(coinListViewModel: CoinListViewModel) {
    var searchInput by remember { mutableStateOf( "" )}

    Row(modifier = Modifier
        .padding(horizontal = 8.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colors.background)) {
       TextField(value = searchInput,
           onValueChange =
           {
               searchInput = it
               coinListViewModel.coins.filter {
                   it.value.info.name.contains(searchInput) or
                   it.value.info.symbol.substring(4, it.value.info.symbol.length)
                       .contains(searchInput.uppercase(Locale.getDefault()))
               }

           },
           singleLine = true, textStyle = MaterialTheme.typography.body1,
           trailingIcon = { IconButton(onClick = { }) { Icon(imageVector = Icons.Default.Search, contentDescription = "ic_search") }},
           colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background),
           placeholder = { Text("코인명/심볼 검색") },
           modifier = Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colors.background))
    }
}

@Composable
fun CoinSortBar(coinListViewModel: CoinListViewModel, sortState: SortState, sort: (SortState) -> Unit) {

    Row() {
        Text("한글명")
        Spacer(Modifier.weight(1f))
        Text("현재가",
            Modifier.clickable {
                when (sortState) {
                    SortState.DESCENDING_PRICE -> {
                        sort(SortState.ASCENDING_PRICE)
                    }
                    SortState.ASCENDING_PRICE -> {
                        sort(SortState.DEFAULT)
                    }
                    else -> {
                        sort(SortState.DESCENDING_PRICE)
                    }
                }
            }
        )
        Spacer(Modifier.weight(1f))
        Text("전일대비",
            Modifier.clickable{
                when (sortState) {
                    SortState.DESCENDING_RATE -> {
                        sort(SortState.ASCENDING_RATE)
                    }
                    SortState.ASCENDING_RATE -> {
                        sort(SortState.DEFAULT)
                    }
                    else -> {
                        sort(SortState.DESCENDING_RATE)
                    }
                }
            })
        Spacer(Modifier.weight(1f))
        Text("거래대금",
            Modifier.clickable{
                when (sortState) {
                    SortState.DESCENDING_VOLUME -> {
                        sort(SortState.ASCENDING_VOLUME)
                    }
                    SortState.ASCENDING_VOLUME -> {
                        sort(SortState.DEFAULT)
                    }
                    else -> {
                        sort(SortState.DESCENDING_VOLUME)
                    }
                }
            })
    }
}



@Composable
fun AllCoinsListView(coinListViewModel: CoinListViewModel, sortState: SortState, onCoinClicked: (String) -> Unit) {
    val scroll = rememberLazyListState()

    Surface(modifier = Modifier
        .fillMaxSize()
       ) {
        LazyColumn(state = scroll) {
            items(sortedItems(coinListViewModel, sortState)) {
                ItemCoinViewWithoutIcon(
                    coinListViewModel = coinListViewModel,
                    coin = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable {
                            onCoinClicked(it.info.symbol)
                        }
                        .drawBehind {
                            val strokeWidth = 0.3f * density
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                Blue1200,
                                Offset(0f, y),
                                Offset(size.width, y),
                                strokeWidth
                            )
                        }
                        .padding(8.dp),
                )
            }

        }
    }
}

private fun sortedItems(coinListViewModel: CoinListViewModel, sortState: SortState) : List<Coin> {
    return when (sortState) {
        SortState.DEFAULT -> coinListViewModel.coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }
        SortState.DESCENDING_PRICE -> coinListViewModel.coins.values.sortedByDescending { it.ticker.trade_price }
        SortState.ASCENDING_PRICE -> coinListViewModel.coins.values.sortedBy { it.ticker.trade_price }
        SortState.DESCENDING_RATE -> coinListViewModel.coins.values.sortedByDescending { it.ticker.signed_change_rate }
        SortState.ASCENDING_RATE -> coinListViewModel.coins.values.sortedBy { it.ticker.signed_change_rate }
        SortState.DESCENDING_VOLUME -> coinListViewModel.coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }
        SortState.ASCENDING_VOLUME -> coinListViewModel.coins.values.sortedBy { it.ticker.acc_trade_price_24h }
    }
}

@Composable
fun ItemCoinViewWithoutIcon(coinListViewModel: CoinListViewModel, coin : Coin, modifier: Modifier) {

    val c = coinListViewModel.coins[coin.info.symbol]!!
    val color = NumberFormat.color(coin.ticker.change)

    val context = LocalContext.current
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {

        Column(Modifier.weight(8f)){
            Text(c.info.name, fontSize = 14.sp)
            Text(SymbolFormat.get(c.info.symbol), fontSize = 14.sp)
        }

        Box(Modifier.weight(5f), contentAlignment = Alignment.CenterEnd) {
            val price = c.ticker.trade_price
            val textFormat = NumberFormat.coinPrice(price)

            Text(textFormat, color = color, fontSize = 14.sp)
        }

        Box(Modifier.weight(5f), contentAlignment = Alignment.CenterEnd) {
            Text(NumberFormat.coinRate(c.ticker.signed_change_rate),
                color = color, fontSize = 14.sp)
        }

        Box(Modifier.weight(6f), contentAlignment = Alignment.CenterEnd) {
            Text(NumberFormat.coinVolume(c.ticker.acc_trade_price_24h), fontSize = 14.sp)
        }


    }
}
