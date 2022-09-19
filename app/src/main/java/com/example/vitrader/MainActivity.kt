package com.example.vitrader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.vitrader.navigation.BottomNavHost
import com.example.vitrader.navigation.BottomNavigationView
import com.example.vitrader.screen.chart.noRippleClickable
import com.example.vitrader.screen.main.getMainScreens
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.model.CoinRepository
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.CircleShape as CircleShape


class MainActivity : ComponentActivity() {
    private lateinit var coinListViewModel: CoinListViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeVariables()

        setContent {
            VitraderTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background) {
                    WholeScreenScaffold(userViewModel, coinListViewModel, auth)
                }
            }
        }
    }

    private fun initializeVariables() {
        coinListViewModel = ViewModelProvider(this)[CoinListViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "OnResume")

    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "OnStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "OnRestart")
        CoinRepository.launchGettingExternalCoinData()
    }
}

@Composable
fun WholeScreenScaffold(userViewModel: UserViewModel, coinListViewModel: CoinListViewModel, auth: FirebaseAuth) {

    val navController = rememberNavController()
    val mainScreenDestinationList = getMainScreens()
    Scaffold(
        topBar = {
            MainTopAppBar(auth)
        },
        bottomBar = {
            BottomNavigationView(navController, mainScreenDestinationList)
        }
    ) {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            BottomNavHost(userViewModel, coinListViewModel, navController)
        }
    }

}


@Composable
fun MainTopAppBar(auth: FirebaseAuth) {
    TopAppBar(modifier = Modifier.background(color = Color(0xff1A1B2F))) {
        Text("Vitrader",
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .weight(1f),
            fontSize = 20.sp)
        Image(painterResource(id = R.drawable.crying_cat),
            contentDescription = "profile_image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .size(28.dp)
                .clip(CircleShape)
                .noRippleClickable {
                    auth.currentUser?.email?.let { Log.d("TopBar", it) }
                })
    }
}


