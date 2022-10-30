package com.example.vitrader.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vitrader.TransactionActivity
import com.example.vitrader.R
import com.example.vitrader.screen.main.*
import com.example.vitrader.theme.Blue1600
import com.example.vitrader.theme.Blue2000
import com.example.vitrader.utils.db.UpbitWebSocketListener
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel

fun moveToTransactionActivity(context: Context, symbol: String) {
    val intent = Intent(context, TransactionActivity::class.java)
    val bundle = Bundle()
    bundle.apply{
        putString("symbol", symbol)
    }
    intent.putExtras(bundle)
    context.startActivity(intent)
}

@Composable
fun BottomNavHost(userProfileViewModel: UserProfileViewModel, userAccountViewModel: UserAccountViewModel, coinListViewModel: CoinListViewModel, navController: NavHostController) {

    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = MainScreenDestination.HOT_COINS.route,
        modifier = Modifier.padding(0.dp)
    ){

        val onCoinClicked: (String) -> Unit = { moveToTransactionActivity(context, it) }

        composable(MainScreenDestination.HOT_COINS.route) {
            HotCoinsScreen(coinListViewModel) { onCoinClicked(it) }
        }
        composable(MainScreenDestination.COIN_LIST.route) {
            CoinListScreen(coinListViewModel, userAccountViewModel, userProfileViewModel) { onCoinClicked(it) }
        }
        composable(MainScreenDestination.RANKING.route) {
            RankingScreen()
        }
        composable(MainScreenDestination.PROFILE.route) {
            ProfileScreen(userAccountViewModel, coinListViewModel)
        }

    }
}

@Composable
fun BottomNavigationView(navController: NavHostController, mainScreenList: List<MainScreenDestination>) {
    var currentMainScreen: MainScreenDestination by remember { mutableStateOf(MainScreenDestination.HOT_COINS) }
    val screenIconList = listOf(
        R.drawable.ic_home,
        R.drawable.ic_chart,
        R.drawable.ic_crown,
        R.drawable.ic_profile
    )
    val selectedItem = remember { mutableStateListOf(true, false, false, false) }

    BottomNavigation() {
        for(item in mainScreenList.indices){
            BottomNavigationItem(
                selected = selectedItem[item],
                onClick =  {
                    if(selectedItem[item]) return@BottomNavigationItem
                    navController.popBackStack()
                    currentMainScreen = mainScreenList[item]
                    navController.navigate(currentMainScreen.route)

                    for (i in 0 until selectedItem.size)
                        selectedItem[i] = false
                    selectedItem[item] = true

                },

                icon = {
                    Icon(painter = painterResource(id = screenIconList[item]),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp))
                },
                modifier = Modifier.background(Blue1600),
                unselectedContentColor = Blue2000
            )
        }
    }
}

@Composable
fun LoadingView() {
    Text("로딩중")
}


