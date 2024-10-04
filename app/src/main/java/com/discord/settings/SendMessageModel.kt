package com.discord.settings

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SendMessageModel(private val token:String,private val channelId:String, private val client: OkHttpClient, private val model:String) {
    private val eventName = "DiscordConnection"

    //send modal to user
    fun sendModalToUser(interactionId: String, interactionToken: String,option:String,values:Array<String>){
        // The callback URL with interaction ID and token
        val url = "https://discord.com/api/v10/interactions/$interactionId/$interactionToken/callback"
        var input="""""" //setting the whole input in modal

        //building input for modal
        for ((i,value) in values.withIndex()){
            input+="""{"type": 1,"components": [{"type": 4,"custom_id": "input_field_$i","label": "$value","style": 1,"min_length": 1,"max_length": 100,"placeholder": "$value...","required": true}]},"""
        }

        //json for modal
        val json = """{
                "type": 9,
                "data": {
                    "custom_id": "$model showModal $option",
                    "title": "$option (${values.size})",
                    "components": [${input.substring(0, input.length - 1)}]
                }
            }""".trimIndent()

        // Create the request body
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Create the HTTP request
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bot $token")
            .post(body)
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(eventName, "sendMessage : Error sending message: ${e.message}")

            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d(eventName, "sendMessage : Message sent: $responseBody")
                    }
                } else {
                    Log.e(eventName, "Failed to send message: ${response.code} ${response.message}")
                    response.body?.string()?.let { errorBody ->
                        Log.e(eventName, "sendMessage : Error details: $errorBody")
                    }
                }
            }
        })
    }



    //send options button
    fun sendOptionsButton(event:String=""){
        val url = "https://discord.com/api/v10/channels/$channelId/messages" //default messageUrl (sending normal message)
        val buttonJson = """
        {
          "type": 1,
          "components": [
            {
              "type": 2,
              "label": "$model (Click to get Options) $event",
              "style": 3,
              "custom_id": "$model showOptions"
            }
                        ]
        }""".trimIndent()
        val json = """{"content": "", "components": [$buttonJson]}"""
        val requestBody =json.toRequestBody("application/json; charset=utf-8".toMediaType())

        json.toRequestBody("application/json; charset=utf-8".toMediaType())
        // Build the HTTP request
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bot $token")
            .post(requestBody)
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(eventName, "sendMessage : Error sending message: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d(eventName, "sendMessage : Message sent: $responseBody")
                    }
                } else {
                    Log.e(eventName, "Failed to send message: ${response.code} ${response.message}")
                    response.body?.string()?.let { errorBody ->
                        Log.e(eventName, "sendMessage : Error details: $errorBody")
                        Log.e(eventName,"JSON : $json")
                    }
                }
            }
        })

    }



    //sends for plain message and interaction response
     fun sendMessage(content: String="", mode: Int = 0,interactionId:String="",interactionToken:String?="") {

        val messageUrl = "https://discord.com/api/v10/channels/$channelId/messages" //default messageUrl (sending normal message)


        val isInteractionResponse:Boolean = interactionId!="" && interactionToken!="" //check if this is a interaction response or normal message

        val url = if(isInteractionResponse)"https://discord.com/api/v10/interactions/$interactionId/$interactionToken/callback" else messageUrl//set message url for interaction/normal response

        val json = when (mode) {

            1 -> OptionsMenu().optionsJson(model) //options dropdown menu
            else -> """{"content":"**$model**```$content```"}""" //normal message
        }

        val requestBody = if(isInteractionResponse)"""{"type": 4,"data": $json}""".toRequestBody("application/json; charset=utf-8".toMediaType()) else json.toRequestBody("application/json; charset=utf-8".toMediaType())//set request body for interaction/normal response

        // Build the HTTP request
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bot $token")
            .post(requestBody)
            .build()

        Log.d(eventName,"JSON : $json")

        // Send the request asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(eventName, "sendMessage : Error sending message: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d(eventName, "sendMessage : Message sent: $responseBody")
                    }
                } else {
                    Log.e(eventName, "Failed to send message: ${response.code} ${response.message}")
                    response.body?.string()?.let { errorBody ->
                        Log.e(eventName, "sendMessage : Error details: $errorBody")
                        Log.e(eventName,"JSON : $json")
                    }
                }
            }
        })
    }

}