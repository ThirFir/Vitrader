package com.example.vitrader.screen.main

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.vitrader.utils.*


@Composable
fun RankingScreen() {

    LazyColumn{
        items(Rankers.rankers) {
            RankProfile(it)
        }
    }

}

@Composable
fun RankProfile(ranker: Ranker) {

    val context = LocalContext.current

    Rankers.rankerNickname
    val nickname by remember { mutableStateOf(Rankers.rankerNickname[ranker.uid]) }
    val bitmap by remember { mutableStateOf(Rankers.rankerProfileImage[ranker.uid]) }

    
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp)
        .noRippleClickable {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("uid", ranker.uid)
            }
            context.startActivity(intent)
        },
        verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
            verticalAlignment = Alignment.CenterVertically) {

            val rankColor =
                when(ranker.rank) {
                    1 -> Color(255, 215, 0)
                    2 -> Color(192, 192, 192)
                    3 -> Color(210, 105, 30)
                    else -> Color.White
                }

            Text(ranker.rank.toString(),
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

        AutoResizeText(NumberFormat.krwFormat(ranker.krw) + " KRW", color = Color.White, maxLines = 1, modifier = Modifier.width(150.dp), targetTextSizeHeight = 13.5.sp, textAlign = TextAlign.End)
    }
}