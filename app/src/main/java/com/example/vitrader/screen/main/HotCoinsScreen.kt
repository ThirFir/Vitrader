package com.example.vitrader.screen.main

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.R
import com.example.vitrader.theme.Blue1800
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.NumberFormat
import kotlin.collections.List

@Composable
fun HotCoinsScreen(coinListViewModel: CoinListViewModel, onCoinClicked: (String) -> Unit = {}) {

    Column(modifier = Modifier
        .padding(bottom = 20.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (coinListViewModel.coins.size >= 5) {
            HottestCoinView(coinListViewModel.mostActiveCoin, onCoinClicked)
            Spacer(Modifier.weight(1f))
            HotCoinsListView(coinListViewModel.activeCoins, onCoinClicked)
        }

    }
}

@Composable
fun HottestCoinView(hottestCoin : Coin, onClicked : (String) -> Unit) {

    val color = NumberFormat.color(hottestCoin.ticker.change)

    Surface(color = MaterialTheme.colors.secondary,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.secondary),
        modifier = Modifier
            .padding(vertical = 27.dp)
            .size(width = 320.dp, height = 180.dp)
            .clip(RoundedCornerShape(20.dp))) {

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                onClicked(hottestCoin.info.symbol)
            }
            .padding(27.dp)) {
            Row() {
                Column(Modifier.weight(1f)) {
                    Text(hottestCoin.info.name + " " + hottestCoin.info.symbol.substring(4, hottestCoin.info.symbol.length),
                        fontSize = 15.sp,
                        modifier = Modifier.heightIn(17.dp))
                    Spacer(modifier = Modifier.size(30.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = "₩ " + NumberFormat.coinPrice(hottestCoin.ticker.trade_price),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.heightIn(22.dp))
                        Spacer(Modifier.size(10.dp))
                        Text(text = NumberFormat.coinPrice(hottestCoin.ticker.signed_change_price),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.heightIn(22.dp),
                            color = color)
                    }
                    Spacer(modifier = Modifier.size(25.dp))

                    Text(text = NumberFormat.coinRate(hottestCoin.ticker.signed_change_rate),
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        modifier = Modifier.heightIn(15.dp),
                        color = color)

                }
                Image(bitmap = hottestCoin.image?.asImageBitmap() ?: ImageBitmap(1, 1),
                    contentDescription = "ic_coin",
                    modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun HotCoinsListView(hotCoinList : List<Coin>, onClicked : (String) -> Unit) {
    Column() {

        LazyColumn(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            items(hotCoinList) {
                ItemCoinViewWithIcon(coin = it, onClicked)
            }
        }
    }
}

@Composable
fun ItemCoinViewWithIcon(coin : Coin, onClicked : (String) -> Unit) {

    Card(Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Blue1800)
        .clickable { onClicked(coin.info.symbol) }
    ) {
        Row(Modifier
            .padding(horizontal = 12.dp)
            .height(72.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(bitmap = coin.image?.asImageBitmap() ?: ImageBitmap(1, 1),
                contentDescription = null,
                Modifier
                    .padding(4.dp)
                    .size(32.dp))
            Spacer(Modifier.size(6.dp))
            Text(coin.info.symbol.substring(4, coin.info.symbol.length), fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(6.dp))
            Text(coin.info.name, fontWeight = FontWeight.Normal)

            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                val price = coin.ticker.trade_price

                Text(NumberFormat.coinPrice(price), fontWeight = FontWeight.Light)

                Text(NumberFormat.coinRate(coin.ticker.signed_change_rate),
                    fontSize = 14.5.sp,
                    color = NumberFormat.color(coin.ticker.change))
            }
        }
    }
}
