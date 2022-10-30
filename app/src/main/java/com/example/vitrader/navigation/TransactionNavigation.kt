package com.example.vitrader.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.screen.transaction.ChartScreen
import com.example.vitrader.screen.transaction.TransactionScreen
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.OrderBookViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionViewPager(coinViewModel: CoinViewModel, orderBookViewModel: OrderBookViewModel, userAccountViewModel: UserAccountViewModel, userProfileViewModel: UserProfileViewModel) {
    Scaffold(topBar = { TransactionTopAppBar(coinViewModel, userProfileViewModel) }) {

        Column() {
            TransactionSecondaryTopAppBar(coinViewModel)
            TransactionNavigationTabLayout(coinViewModel, orderBookViewModel, userAccountViewModel)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionNavigationTabLayout(coinViewModel: CoinViewModel, orderBookViewModel: OrderBookViewModel, userAccountViewModel: UserAccountViewModel) {

    val TRANSACTION_PAGE = 0
    val CHART_PAGE = 1

    val titles = listOf("주문", "차트")
    var selectedTab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize(), Arrangement.Center) {
        TabRow(selectedTabIndex = selectedTab ) {
            titles.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = {
                    selectedTab = index
                }) {
                    Text(title)
                }
            }
        }
        Box(Modifier.fillMaxSize()) {
            if(selectedTab == TRANSACTION_PAGE)
                TransactionScreen(coinViewModel, orderBookViewModel, userAccountViewModel)
            else if(selectedTab == CHART_PAGE)
                ChartScreen(coinViewModel, userAccountViewModel)
        }

    }
}

@Composable
fun TransactionTopAppBar(coinViewModel: CoinViewModel, userProfileViewModel: UserProfileViewModel) {
    val symbol = coinViewModel.coin?.info?.symbol
    var bookmark by remember { mutableStateOf(userProfileViewModel.bookmark.contains(symbol)) }

    TopAppBar(modifier = Modifier.background(color = Color(0xff1A1B2F))) {
        Column() {

            Row() {

                // 코인 이름 + 심볼
                Text(text = (coinViewModel.coin?.info?.name ?: "Unknown") + " " + symbol,
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .weight(1f),
                    fontSize = 20.sp)


                val tint = if(bookmark) Color.Yellow
                else Color.LightGray

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "ic_like",
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .size(28.dp)
                        .noRippleClickable {
                            bookmark = !bookmark
                            userProfileViewModel.bookmark(symbol)
                        },
                    tint = tint
                )
            }
        }
    }
}

// 코인 ticker 정보 TopAppBar. 상단 고정
@Composable
fun TransactionSecondaryTopAppBar(coinViewModel: CoinViewModel) {

    val color = NumberFormat.color(coinViewModel.coin?.ticker?.change ?: "")

    Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            // 현재가
            Text(text = NumberFormat.coinPrice(coinViewModel.coin?.ticker?.trade_price ?: 0.0),
                fontSize = 20.sp, color = color)

            Row() {
                // 전일 대비 %
                Text(text = NumberFormat.coinRate(coinViewModel.coin?.ticker?.signed_change_rate ?: 0.0),
                    color = color)
                Spacer(Modifier.width(12.dp))

                // 전일 대비 가격
                Text(text = NumberFormat.coinPrice(coinViewModel.coin?.ticker?.signed_change_price ?: 0.0),
                    color = color)
            }
        }
        Image(bitmap = coinViewModel.coin?.image?.asImageBitmap() ?: ImageBitmap(1, 1),
            contentDescription = "ic_coin",
            modifier = Modifier.size(40.dp))
    }
}