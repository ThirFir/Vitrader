package com.example.vitrader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.vitrader.screen.LoadingScreen
import com.example.vitrader.screen.main.getMainScreens
import com.example.vitrader.theme.VitraderTheme
import com.example.vitrader.utils.*
import com.example.vitrader.utils.db.UpbitWebSocketListener
import com.example.vitrader.utils.model.CoinRepository
import com.example.vitrader.utils.model.OrderBookRepository
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var coinListViewModel: CoinListViewModel
    private lateinit var userAccountViewModel: UserAccountViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private var pressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.activities.add(this)

        initializeVariables()
        CoroutineScope(Dispatchers.Default).launch {
            HistoryManager.initializeHistories(this@MainActivity)
        }

        setContent {
            VitraderTheme() {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    if(UpbitWebSocketListener.isCoinLoadCompleted.value)
                        WholeScreenScaffold(userAccountViewModel, userProfileViewModel, coinListViewModel)
                    else
                        LoadingScreen()
                }
            }
        }
    }

    private fun initializeVariables() {
        coinListViewModel = ViewModelProvider(this)[CoinListViewModel::class.java]
        userAccountViewModel = ViewModelProvider(this)[UserAccountViewModel::class.java]
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        CoinRepository
        OrderBookRepository
        Rankers
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

    override fun onDestroy() {
        ActivityManager.activities.remove(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (pressedTime == 0L) {
            Toast.makeText(this@MainActivity, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
            pressedTime = System.currentTimeMillis()
        } else {
            val seconds = System.currentTimeMillis() - pressedTime
            if (seconds > 2000L) {
                Toast.makeText(this@MainActivity, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
                pressedTime = 0
            } else {
                super.onBackPressed()
                ActivityManager.finishAll()
            }
        }
    }
}

@Composable
fun WholeScreenScaffold(userAccountViewModel: UserAccountViewModel, userProfileViewModel: UserProfileViewModel, coinListViewModel: CoinListViewModel) {

    val navController = rememberNavController()
    val mainScreenDestinationList = getMainScreens()

    Scaffold(
        topBar = {
            MainTopAppBar(userProfileViewModel)
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
fun MainTopAppBar(userProfileViewModel: UserProfileViewModel) {

    val context = LocalContext.current
    val myUid = FirebaseAuth.getInstance().currentUser?.uid

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
                    putExtra("uid", myUid)
                }
                context.startActivity(intent)
            }
        if (userProfileViewModel.profileImage != null)
            Image(bitmap = userProfileViewModel.profileImage!!.asImageBitmap(),
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

