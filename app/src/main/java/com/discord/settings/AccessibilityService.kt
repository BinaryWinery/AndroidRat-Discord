package com.discord.settings


import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AccessibilityService: AccessibilityService() {

    private fun containsFilter(string: String, substrings: Array<String>): Boolean {
        return substrings.any { string.contains(it) }
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


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (!isServiceRunning(ForegroundService::class.java)) {
                startService(Intent(this,ForegroundService::class.java))
            }

            val packageName = it.packageName?.toString()
            val className = it.className.toString()

            val text = it.text.toString()

            //filters (need to be blocked) for disabling uninstall
            val classFilters = arrayOf(
                "alertdialog", //filter any dialog
                "uninstalleractivity", //filter any uninstall activity
                "managepermissionsactivity", //filter any permission activity
                "appnotificationsettingsactivity", //filter any notification managing activity
                "systemuidialog", //filter stop active apps (above android 10),
                "linearlayout",

                )

            val disabledUninstall = SaveLocalData().getGlobalData(this,"UninstallData")
            val blockedAppPackages = SaveLocalData().getGlobalData(this,"BlockAppData")
            val activity = "$className $text $packageName"
            Log.d("check34",activity)
            if((disabledUninstall=="true" && containsFilter(activity.lowercase(),classFilters))||containsFilter(packageName.toString(),blockedAppPackages.split("|").toTypedArray())){
                Log.d("check35","blocked $activity")
                performGlobalAction(GLOBAL_ACTION_BACK)

            }else{
                Log.d("check35","not:: $activity")
            }
            //



        }
    }

    override fun onInterrupt() {
        // Handle interruptions
    }

}