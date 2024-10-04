package com.discord.settings


import android.content.Context

class SaveLocalData {
    fun saveGlobalData(context:Context, event:String, data:String){
        val sharedPreferences = context.getSharedPreferences(event, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("activity", data)
        editor.apply()
    }

    fun getGlobalData(context: Context,event:String):String{
        val sharedPreferences = context.getSharedPreferences(event, Context.MODE_PRIVATE)
        return sharedPreferences.getString("activity", null).toString()
    }


}