package com.example.vitrader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.vitrader.navigation.BottomNavHost
import com.example.vitrader.navigation.BottomNavigationView
import com.example.vitrader.screen.main.getMainScreens
import com.example.vitrader.screen.transaction.HistoryManager
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.db.HistoryDatabase
import com.example.vitrader.utils.model.History
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape as CircleShape


class MainActivity : ComponentActivity() {
    private lateinit var coinListViewModel: CoinListViewModel
    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeVariables()
        CoroutineScope(Dispatchers.IO).launch {
            HistoryManager.initializeHistories(this@MainActivity)
        }

        setContent {
            VitraderTheme() {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background) {
                    WholeScreenScaffold(userAccountViewModel, userProfileViewModel, coinListViewModel, auth)
                }
            }
        }
    }

    private fun initializeVariables() {
        coinListViewModel = ViewModelProvider(this)[CoinListViewModel::class.java]
        userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]

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
    }
}

@Composable
fun WholeScreenScaffold(userAccountViewModel: UserAccountViewModel, userProfileViewModel: UserProfileViewModel, coinListViewModel: CoinListViewModel, auth: FirebaseAuth) {

    val navController = rememberNavController()
    val mainScreenDestinationList = getMainScreens()

    Scaffold(
        topBar = {
            MainTopAppBar(auth, userProfileViewModel)
        },
        bottomBar = {
            BottomNavigationView(navController, mainScreenDestinationList)
        }
    ) {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            BottomNavHost(userProfileViewModel, userAccountViewModel, coinListViewModel, navController)
        }
    }

}


@Composable
fun MainTopAppBar(auth: FirebaseAuth, userProfileViewModel: UserProfileViewModel) {

    val context = LocalContext.current

    TopAppBar(modifier = Modifier.background(color = Color(0xff1A1B2F))) {
        Text("Vitrader",
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .weight(1f),
            fontSize = 20.sp)

        val imageModifier = Modifier
            .padding(horizontal = 18.dp)
            .size(32.dp)
            .clip(CircleShape)
            .noRippleClickable {
                val intent = Intent(context, ProfileActivity::class.java).apply {
                    putExtra("email", "")
                }
                context.startActivity(intent)
            }
        if (userProfileViewModel.profileImg != null)
            Image(bitmap = userProfileViewModel.profileImg!!.asImageBitmap(),
                contentDescription = "profile_img",
                contentScale = ContentScale.Crop,
                modifier = imageModifier)
        else
            Image(imageVector = Icons.Default.AccountCircle,
                contentDescription = "profile_img",
                contentScale = ContentScale.Crop,
                modifier = imageModifier)
    }
}

