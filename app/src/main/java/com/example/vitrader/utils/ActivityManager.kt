package com.example.vitrader.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.vitrader.LoginActivity

object ActivityManager {
    val activities = mutableListOf<Activity>()

    fun returnToLoginActivity(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java))
        val count = activities.size
        for(a in 0 until count - 1) {
            activities.first().finish()
            activities.removeFirst()
        }
    }

    fun finishAll() {
        for(a in activities)
            a.finish()
    }
}