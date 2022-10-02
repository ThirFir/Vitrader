package com.example.vitrader.screen


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.vitrader.R
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.example.vitrader.utils.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

@Composable
fun ProfileScreen(userAccountViewModel: UserAccountViewModel, userProfileViewModel: UserProfileViewModel) {

    val context = LocalContext.current

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference


    Surface(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()) {
            UserProfileView(userAccountViewModel, userProfileViewModel)
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
fun UserProfileView(userAccountViewModel: UserAccountViewModel, userProfileViewModel: UserProfileViewModel) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var profileBitmap by remember { mutableStateOf(userProfileViewModel.profileImg) }


    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.let { uri ->
                profileBitmap = uri.parseBitmap(context)
                userProfileViewModel.setProfileImg(profileBitmap)
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

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(userProfileViewModel.nickname, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Column() {
                if (profileBitmap != null)
                    Image(bitmap = profileBitmap!!.asImageBitmap(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "profile_img",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape))
                Button(onClick = {
                    galleryLauncher.launch(intent)
                }) {
                    Text("수정")
                }
            }
        }
    }
}

fun bitmapToString(bitmap: Bitmap): String {

    val byteArrayOutputStream = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    val byteArray = byteArrayOutputStream.toByteArray()

    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@SuppressLint("UseCompatLoadingForDrawables")
fun stringToBitmap(encodedString: String): Bitmap {

    val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)

    return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
}
