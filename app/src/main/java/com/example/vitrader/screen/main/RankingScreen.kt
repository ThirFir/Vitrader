package com.example.vitrader.screen.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitrader.ProfileActivity
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.Ranker
import com.example.vitrader.utils.Rankers
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage



@Composable
fun RankingScreen() {

    LazyColumn{
        itemsIndexed(Rankers.rankers) { rank, user ->
            RankProfile(rank + 1, user)
        }
    }

}

@Composable
fun RankProfile(rank: Int, user: Ranker) {

    val context = LocalContext.current

    Rankers.rankerNickname
    val nickname by remember { mutableStateOf(Rankers.rankerNickname[user.uid]) }
    val bitmap by remember { mutableStateOf(Rankers.rankerProfileImage[user.uid]) }

    
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp)
        .noRippleClickable {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("uid", user.uid)
            }
            context.startActivity(intent)
        },
        verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
            verticalAlignment = Alignment.CenterVertically) {

            val rankColor =
                when(rank) {
                    1 -> Color(255, 215, 0)
                    2 -> Color(192, 192, 192)
                    3 -> Color(210, 105, 30)
                    else -> Color.White
                }

            Text(rank.toString(),
                modifier = Modifier
                    .width(50.dp)
                    .padding()
                    .padding(horizontal = 8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = rankColor,
                textAlign = TextAlign.Center
               )


            if (bitmap == null)
                Icon(imageVector = Icons.Default.AccountCircle,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(
                            CircleShape)
                        .size(40.dp))
            else
                Image(bitmap = bitmap!!.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(
                            CircleShape)
                        .size(40.dp))
            Text(nickname ?: "Unknown", color = Color.White, maxLines = 1, modifier = Modifier.weight(1f), fontSize = 13.5.sp)

        }

        Text(NumberFormat.krwFormat(user.krw) + " KRW", color = Color.White, maxLines = 1, modifier = Modifier.width(150.dp), fontSize = 13.5.sp, textAlign = TextAlign.End)
    }
}