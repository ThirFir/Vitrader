package com.example.vitrader.screen


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.utils.*
import com.example.vitrader.utils.model.UserAccountData
import com.example.vitrader.utils.model.UserProfileData
import com.example.vitrader.utils.viewmodel.CoinListViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ProfileScreen(userAccount: UserAccountData, userProfile: UserProfileData, userProfileViewModel: UserProfileViewModel, coinListViewModel: CoinListViewModel, uid: String) {

    Scaffold(
        topBar = { ProfileTopAppBar() }
    ) {

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                UserProfileView(userAccount, userProfile, userProfileViewModel, coinListViewModel, uid)
            }
        }
    }
}

@Composable
fun ProfileTopAppBar() {
    TopAppBar(modifier = Modifier.background(color = Color(0xff1A1B2F))) {
        Text("Vitrader",
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .weight(1f),
            fontSize = 20.sp)
        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
        }
    }
}

@Suppress("DEPRECATION", "NewApi")
private fun Uri.parseBitmap(context: Context): Bitmap {
    return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 28
        true -> {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        }
        else -> {
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }
    }
}

@Composable
fun UserProfileView(userAccount: UserAccountData, userProfile: UserProfileData, userProfileViewModel: UserProfileViewModel, coinListViewModel: CoinListViewModel, uid: String) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var profileBitmap by remember { mutableStateOf(userProfile.profileImg) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                profileBitmap = uri.parseBitmap(context)
                userProfileViewModel.updateProfileImage(profileBitmap)
            }
        }
    }

    val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
        type = "image/*"
        action = Intent.ACTION_GET_CONTENT
        putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/jpeg", "image/png", "image/bmp", "image/webp")
        )
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (profileBitmap != null)
                Image(bitmap = profileBitmap!!.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .noRippleClickable {
                            galleryLauncher.launch(intent)
                        })
            else
                Image(imageVector = Icons.Default.AccountCircle,
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .noRippleClickable {
                            galleryLauncher.launch(intent)
                        })

            Text(userProfile.nickname, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier
                .padding()
                .padding(vertical = 20.dp))
            Text("아름드리 나래 아름드리 옅구름 여우비 로운 곰다시 예그리나 아리아 달볓 비나리 아리아 컴퓨터 가온해 별하 비나리 미리내 늘품 나래 여우별 우리는 옅구름 감사합니다" +
                    " 나래 곰다시 별빛 나비잠 노트북 도르레 도서 달볓 사과 바람꽃 도서 책방 옅구름 감사합니다 감또개 도서관 감사합니다 포도 노트북 그루잠 감사합니다" +
                    " 감사합니다 여우비 이플 그루잠 비나리 곰다시.", fontSize = 14.sp, maxLines = 3, modifier = Modifier.width(250.dp).height(60.dp))
        }
        Spacer(Modifier.height(30.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("보유 KRW", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                AutoResizeText(NumberFormat.krwFormat(userAccount.krw), textStyle = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("랭킹", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text("${Rankers.getRank(uid)}위", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }
        }
        Spacer(Modifier.height(30.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text("보유 중인 코인", fontWeight = FontWeight.Bold)
            }
            for (possess in userAccount.possessingCoins) {
                val coin = coinListViewModel.coins[possess.key]!!
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                    AutoResizeText(coin.info.name, modifier = Modifier.width(80.dp))
                    AutoResizeText(SymbolFormat.get(possess.key), modifier = Modifier.width(80.dp))
                    Image(coin.image!!.asImageBitmap(), contentDescription = null)

                }
            }
        }

        Spacer(Modifier.height(30.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(HistoryManager.histories) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // HistoryItem(it)
                }
            }
        }
        Button(onClick = {
            auth.signOut()
            ActivityManager.returnToLoginActivity(context)
        }){
            Text("로그아웃")
        }
    }
}
