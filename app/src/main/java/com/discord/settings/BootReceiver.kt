package com.discord.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("check1", "Boot completed detected, starting service.")
            val startIntent = Intent(context, ForegroundService::class.java)
            context?.startForegroundService(startIntent)




        }
    }
}