package com.example.vitrader.screen.main


import androidx.compose.runtime.Composable

enum class MainScreenDestination(val route: String, val description: String, val screen : @Composable () -> Unit){
    HOT_COINS("hot_coins", "hot coins", { HotCoinsScreen(androidx.lifecycle.viewmodel.compose.viewModel()) {} }),
    COIN_LIST("coin_list", "coin list", { CoinListScreen(androidx.lifecycle.viewmodel.compose.viewModel(), androidx.lifecycle.viewmodel.compose.viewModel()) {} } ),
    RANKING("ranking", "ranking", { RankingScreen(androidx.lifecycle.viewmodel.compose.viewModel()) }),
    PROFILE("profile", "profile", { ProfileScreen(androidx.lifecycle.viewmodel.compose.viewModel(), androidx.lifecycle.viewmodel.compose.viewModel()) });


}

fun getMainScreens(): List<MainScreenDestination> = listOf(
    MainScreenDestination.HOT_COINS,
    MainScreenDestination.COIN_LIST,
    MainScreenDestination.RANKING,
    MainScreenDestination.PROFILE)