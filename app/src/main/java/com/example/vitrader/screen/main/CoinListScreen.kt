package com.example.vitrader.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.theme.Blue1200
import com.example.vitrader.theme.Blue2000
import com.example.vitrader.utils.*
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
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
enum class ListTab {
    ALL,
    BOOKMARK,
    POSSESS
}

@Composable
fun CoinListScreen(coinListViewModel: CoinListViewModel, userAccountViewModel: UserAccountViewModel, onCoinClicked: (String) -> Unit) {

    Column(modifier = Modifier
        .padding(0.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var search by remember { mutableStateOf("") }
        CoinSearchBar(coinListViewModel) { search = it }

        Spacer(Modifier.height(6.dp))

        var tabState by remember { mutableStateOf(ListTab.ALL) }
        CoinListFilterBar(tabState) { tabState = it }

        Spacer(Modifier.height(6.dp))

        val sortState = remember { mutableStateOf(SortState.DEFAULT) }
        CoinSortBar(sortState) { sortState.value = it }

        AllCoinsListView(coinListViewModel, userAccountViewModel, sortState.value, search, tabState, onCoinClicked)

    }
}

@Composable
fun CoinSearchBar(coinListViewModel: CoinListViewModel, onValueChanged: (String) -> Unit) {
    var searchInput by remember { mutableStateOf( "" )}

    Row(modifier = Modifier
        .padding(horizontal = 8.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colors.background)) {
       TextField(value = searchInput,
           onValueChange =
           {
               searchInput = it
               onValueChanged(searchInput)

               coinListViewModel.coins.filter {
                   it.value.info.name.contains(searchInput) or
                   it.value.info.symbol.substring(4, it.value.info.symbol.length)
                       .contains(searchInput.uppercase(Locale.getDefault()))
               }

           },
           singleLine = true, textStyle = LocalTextStyle.current.copy(
               color = Color.White,
               fontSize = 16.sp,
               fontWeight = FontWeight.Bold
           ),
           leadingIcon =  { Icon(imageVector = Icons.Default.Search, contentDescription = "ic_search") },
           colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.background),
           placeholder = { Text("코인명/심볼 검색") },
           modifier = Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colors.background))
    }
}

@Composable
fun CoinListFilterBar(tabState: ListTab, tab: (ListTab) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Spacer(Modifier.width(6.dp))
        Button(onClick = { tab(ListTab.ALL)}, colors = ButtonDefaults.buttonColors(backgroundColor = if(tabState == ListTab.ALL) Blue1200 else Blue2000),
            modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Text("전체", color = Color.White)
        }
        Spacer(Modifier.width(2.dp))
        Button(onClick = { tab(ListTab.BOOKMARK)}, colors = ButtonDefaults.buttonColors(backgroundColor = if(tabState == ListTab.BOOKMARK) Blue1200 else Blue2000),
            modifier = Modifier.clip(RoundedCornerShape(16.dp)) ) {
            Text("관심", color = Color.White)
        }
        Spacer(Modifier.width(2.dp))
        Button(onClick = { tab(ListTab.POSSESS)}, colors = ButtonDefaults.buttonColors(backgroundColor = if(tabState == ListTab.POSSESS) Blue1200 else Blue2000),
            modifier = Modifier.clip(RoundedCornerShape(16.dp)) ) {
            Text("보유", color = Color.White)
        }
    }
}

@Composable
fun CoinSortBar(sortState: MutableState<SortState>, sort: (SortState) -> Unit) {

    Row() {
        Row(Modifier.weight(8f), horizontalArrangement = Arrangement.Center) {
            Text("한글명")
        }
        Row(Modifier
            .weight(6f)
            .noRippleClickable {
                when (sortState.value) {
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
            }, horizontalArrangement = Arrangement.Center) {
            Text("현재가")
            if (sortState.value == SortState.ASCENDING_PRICE)
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
            else if (sortState.value == SortState.DESCENDING_PRICE)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)

        }
        Row(Modifier
            .weight(6f)
            .noRippleClickable {
                when (sortState.value) {
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
            }, horizontalArrangement = Arrangement.Center) {
            Text("전일대비")
            if (sortState.value == SortState.ASCENDING_RATE)
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
            else if (sortState.value == SortState.DESCENDING_RATE)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
        Row(Modifier
            .weight(5f)
            .noRippleClickable {
                when (sortState.value) {
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
            }, horizontalArrangement = Arrangement.Center) {
            Text("거래대금")
            if (sortState.value == SortState.ASCENDING_VOLUME)
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
            else if (sortState.value == SortState.DESCENDING_VOLUME || sortState.value == SortState.DEFAULT)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}


@Composable
fun AllCoinsListView(coinListViewModel: CoinListViewModel, userAccountViewModel: UserAccountViewModel, sortState: SortState, search: String, tab: ListTab, onCoinClicked: (String) -> Unit) {
    val scroll = rememberLazyListState()

    Surface(modifier = Modifier
        .fillMaxSize()
       ) {
        LazyColumn(state = scroll) {
            items(sortedItems(coinListViewModel, userAccountViewModel, sortState, search, tab)) {
                ItemCoinViewWithoutIcon(
                    coinListViewModel = coinListViewModel,
                    coin = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
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

private fun sortedItems(coinListViewModel: CoinListViewModel, userAccountViewModel: UserAccountViewModel, sortState: SortState, search: String, tab: ListTab) : List<Coin> {

    return when (sortState) {
        SortState.DEFAULT -> coinListViewModel.coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }
        SortState.DESCENDING_PRICE -> coinListViewModel.coins.values.sortedByDescending { it.ticker.trade_price }
        SortState.ASCENDING_PRICE -> coinListViewModel.coins.values.sortedBy { it.ticker.trade_price }
        SortState.DESCENDING_RATE -> coinListViewModel.coins.values.sortedByDescending { it.ticker.signed_change_rate }
        SortState.ASCENDING_RATE -> coinListViewModel.coins.values.sortedBy { it.ticker.signed_change_rate }
        SortState.DESCENDING_VOLUME -> coinListViewModel.coins.values.sortedByDescending { it.ticker.acc_trade_price_24h }
        SortState.ASCENDING_VOLUME -> coinListViewModel.coins.values.sortedBy { it.ticker.acc_trade_price_24h }
    }.filter { it.info.name.contains(search) or                             // 검색 filter
            it.info.symbol.substring(4, it.info.symbol.length)
                .contains(search.uppercase(Locale.getDefault())) }
        .filter {                                                   // 탭 filter
            when (tab) {
                ListTab.ALL -> true
                ListTab.BOOKMARK -> { userAccountViewModel.bookmark.contains(it.info.symbol) }
                ListTab.POSSESS -> { userAccountViewModel.possessingCoins.contains(it.info.symbol)}
            }
        }
}

@Composable
fun ItemCoinViewWithoutIcon(coinListViewModel: CoinListViewModel, coin : Coin, modifier: Modifier) {

    val c = coinListViewModel.coins[coin.info.symbol]!!
    val color = NumberFormat.color(coin.ticker.change)

    Row(modifier = modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Column(Modifier.weight(8f)){
            Text(c.info.name, fontSize = 14.sp, maxLines = 1)
            Text(SymbolFormat.get(c.info.symbol), fontSize = 14.sp)
        }

        Box(Modifier.weight(5f), contentAlignment = Alignment.CenterEnd) {
            val price = c.ticker.trade_price
            val textFormat = NumberFormat.coinPrice(price)

            Text(textFormat, color = color, fontSize = 14.sp, maxLines = 1)
        }

        Box(Modifier.weight(5f), contentAlignment = Alignment.CenterEnd) {
            Text(NumberFormat.coinRate(c.ticker.signed_change_rate),
                color = color, fontSize = 14.sp, maxLines = 1)
        }

        Box(Modifier.weight(6f), contentAlignment = Alignment.CenterEnd) {
            Text(NumberFormat.coinVolume(c.ticker.acc_trade_price_24h), fontSize = 14.sp, maxLines = 1)
        }


    }
}
