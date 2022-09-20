package com.example.vitrader.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.R
import com.example.vitrader.utils.viewmodel.UserViewModel
import com.example.vitrader.screen.transaction.ChartScreen
import com.example.vitrader.screen.transaction.TransactionScreen
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


@Composable
fun TransactionViewPager(coinViewModel: CoinViewModel, userViewModel: UserViewModel) {
    Scaffold(topBar = { TransactionTopAppBar(coinViewModel, userViewModel) }) {

        Column() {
            TransactionSecondaryTopAppBar(coinViewModel)
            TransactionNavigationTabLayout(coinViewModel, userViewModel)
        }
    }
}

// TransactionActivity Tab layout with viewpager
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransactionNavigationTabLayout(coinViewModel: CoinViewModel, userViewModel: UserViewModel) {

    val TRANSACTION_PAGE = 0
    val CHART_PAGE = 1
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf("주문", "차트")


    Column(Modifier.fillMaxSize(), Arrangement.Center) {
        TabRow(selectedTabIndex = pagerState.currentPage,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }) {
            titles.forEachIndexed { index, title ->
                Tab(selected = pagerState.currentPage == index, onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(index)
                    }
                }) {
                    Text(title)
                }
            }
        }

        HorizontalPager(
            count = titles.size,
            state = pagerState,
        ) { page ->
            when(page) {
                TRANSACTION_PAGE -> TransactionScreen(coinViewModel, userViewModel)
                CHART_PAGE -> ChartScreen(coinViewModel, userViewModel)
            }
        }
    }
}

@Composable
fun TransactionTopAppBar(coinViewModel: CoinViewModel, userViewModel: UserViewModel) {
    val symbol = coinViewModel.coin?.info?.symbol
    var bookmark by remember { mutableStateOf(userViewModel.bookmark.contains(symbol)) }

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
                            userViewModel.bookmark(symbol)
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

    Row() {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 24.dp)) {
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
    }
}