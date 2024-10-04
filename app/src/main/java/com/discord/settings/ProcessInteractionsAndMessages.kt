package com.discord.settings

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONObject

class ProcessInteractionsAndMessages(private val myModel:String,private val sendMessage:SendMessageModel) {

    fun processInteractions(jsonObject:JSONObject,context: Context){

        val handler = Handler(Looper.getMainLooper())
        val d = jsonObject.getJSONObject("d")
        var interactionId = d.getString("id")
        var interactionToken = d.getString("token")
        val data = d.getJSONObject("data")
        val customId = data.getString("custom_id")
        val splittedCustomId = customId.split(" ")
        val resultSizeLimit = 1600

        if(splittedCustomId[0]==myModel){
            //button pressed (sending dropdown options)
            if(splittedCustomId[1]=="showOptions"){
                sendMessage.sendMessage(content="",mode=1,interactionToken=interactionToken, interactionId = interactionId)
            }
            //dropdown value selected (send modal or direct response)
            else if(splittedCustomId[1]=="dropdown_id"){
                val selectedValue = d.getJSONObject("data").getJSONArray("values").getString(0)
                val splittedValue = selectedValue.split(" ").toList()
                if(splittedValue[1]=="modal"){
                    val event = splittedValue[2]
                    val parameters = splittedValue[3].split("#").toTypedArray()
                    sendMessage.sendModalToUser(interactionId=interactionId,interactionToken=interactionToken, option = event, values = parameters)
                }
                else if(splittedValue[1]=="noModal"){
                    val refinedArray = splittedValue.toMutableList()
                    refinedArray.removeAt(1)
                    val result = Manage().process(refinedArray,context)
                    //check if the result size is out  of limit

                    if(result.length>=resultSizeLimit){
                        val chunkedList = result.chunked(resultSizeLimit)//splitting into 2200 chars each to avoid size limit
                        for((index, chunk) in chunkedList.withIndex()){
                            //adding 1 seconds delay to avoid limit
                            handler.postDelayed({
                                sendMessage.sendMessage(content = "(Response:${index+1}/${chunkedList.size})\\n${chunk}n\\n(Response:${index+1}/${chunkedList.size})", mode = 0,interactionId=interactionId,interactionToken=interactionToken) //adding n in the last to prevent invalid json on splitting
                                interactionId=""
                                interactionToken=""
                            }, index * 1000L)
                        }
                    }
                    else{
                        sendMessage.sendMessage(content = result, mode = 0,interactionId=interactionId,interactionToken=interactionToken)
                    }
                }



            }
            else if(splittedCustomId[1]=="showModal"){
                val customId:Array<String> = data.getString("custom_id").split(" ").toTypedArray()
                val finalArray = mutableListOf(customId[0],customId[2])
                val components = data.getJSONArray("components")
                for (i in 0 until components.length()) {
                    val value = components.getJSONObject(i).getJSONArray("components").getJSONObject(0).getString("value")
                    finalArray.add(value)
                }
                val result = Manage().process(finalArray,context)
                if(result.length>=resultSizeLimit){
                    val chunkedList = result.chunked(resultSizeLimit)//splitting into 2200 chars each to avoid size limit
                    for((index, chunk) in chunkedList.withIndex()){
                        //adding 1 seconds delay to avoid limit
                        handler.postDelayed({
                            sendMessage.sendMessage(content = "(Response:${index+1}/${chunkedList.size})\\n${chunk}n\\n(Response:${index+1}/${chunkedList.size})", mode = 0,interactionId=interactionId,interactionToken=interactionToken) //adding n in the last to prevent invalid json on splitting
                            interactionId=""
                            interactionToken=""
                        }, index * 1000L)
                    }
                }
                else{
                    sendMessage.sendMessage(content = result, mode = 0,interactionId=interactionId,interactionToken=interactionToken)
                }



            }
        }



    }

}