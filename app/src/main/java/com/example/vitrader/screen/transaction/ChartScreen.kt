package com.example.vitrader.screen.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.UserViewModel


@Composable
fun ChartScreen(viewModel: CoinViewModel, userViewModel: UserViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("CHART")
    }
}