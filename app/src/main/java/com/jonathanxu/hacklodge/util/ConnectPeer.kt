package com.jonathanxu.hacklodge.util

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

object ConnectPeer {

    fun connect(
        deviceAddress: String,
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel,
        context: Context,
        listener: WifiP2pManager.ActionListener
    ) {
        val config = WifiP2pConfig()
        config.deviceAddress = deviceAddress
        config.groupOwnerIntent = 0

        Log.d("Connect", "initiated")
        manager.connect(channel, config, listener)

    }

    fun disconnect(manager: WifiP2pManager, channel: WifiP2pManager.Channel) {
        manager.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("Disconnect", "onSuccess")
            }

            override fun onFailure(reason: Int) {
                Log.d("Disconnect ", "onFailure$reason")
            }
        })
    }

}