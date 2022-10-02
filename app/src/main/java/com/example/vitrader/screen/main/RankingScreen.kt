package com.example.vitrader.screen.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitrader.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun RankingScreen() {

    val ranking = remember { mutableStateListOf<DataSnapshot>() }

    val db = FirebaseDatabase.getInstance("https://vitrader-a8d28-default-rtdb.asia-southeast1.firebasedatabase.app")
    val dbRef = db.getReference("krw")
    dbRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            dbRef.orderByValue().limitToFirst(100).get().addOnSuccessListener {
                ranking.clear()
                it.children.forEach{ data ->
                    ranking.add(data)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })

    LazyColumn{
        itemsIndexed(ranking.sortedByDescending { it.value as Long }) { rank, item ->
            RankProfile(rank + 1, item)
        }
    }

}

@Composable
fun RankProfile(rank: Int, item: DataSnapshot) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp),
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

            Image(imageVector = Icons.Default.AccountCircle,
                contentDescription = "profile_img",
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(
                        CircleShape)
                    .size(40.dp))
            Text(item.key.toString())
        }
        Text(item.value.toString() + " KRW")
    }
}