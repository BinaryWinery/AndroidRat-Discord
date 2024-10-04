package com.discord.settings

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log


class NetworkChangeListener(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var startListener = false


    fun registerNetworkCallback(connection:Connection) {
        // Unregister the existing callback if it exists
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if(startListener){ //to skip the first connection on start
                    connection.run(context,"networkListener")
                }
                startListener=true

                Log.d("NetworkMonitor2", "Network Online")

            }

            override fun onLost(network: Network) {
                Log.d("NetworkMonitor2", "Network Lost")
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                Log.d("NetworkMonitor2", "Network Changed")


            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }


}

