package com.example.vitrader.screen.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vitrader.ProfileActivity
import com.example.vitrader.R
import com.example.vitrader.utils.NumberFormat
import com.example.vitrader.utils.db.UserRemoteDataSource
import com.example.vitrader.utils.noRippleClickable
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@Composable
fun RankingScreen(userProfileViewModel: UserProfileViewModel) {

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
            RankProfile(rank + 1, item, userProfileViewModel)
        }
    }

}

@Composable
fun RankProfile(rank: Int, item: DataSnapshot, userProfileViewModel: UserProfileViewModel) {

    val context = LocalContext.current

    val nickname by remember { mutableStateOf("") }

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child(item.key!!)

    var byteArray by remember { mutableStateOf<ByteArray?>(null)}
    storageRef.getBytes(100000000).addOnCompleteListener {
        if(it.isSuccessful) {
            byteArray = it.result
        }
    }

    val bitmap: Bitmap? =
        if (byteArray != null)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        else null

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp)
        .noRippleClickable {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("email", item.key)
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
                Image(bitmap = bitmap.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "profile_img",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(
                            CircleShape)
                        .size(40.dp))
            Text(item.key.toString(), color = Color.White, maxLines = 1, modifier = Modifier.weight(1f), fontSize = 13.5.sp)

        }

        Text(NumberFormat.krwFormat(item.value as Long) + " KRW", color = Color.White, maxLines = 1, modifier = Modifier.width(150.dp), fontSize = 13.5.sp, textAlign = TextAlign.End)
    }
}