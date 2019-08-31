package com.jonathanxu.hacklodge.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

class MyBroadcastReceiver
    (
    private var p2pManager: WifiP2pManager,
    private var channel: WifiP2pManager.Channel,
    private var context: Context,
    private var infoListener: WifiP2pManager.ConnectionInfoListener) : BroadcastReceiver() {
    private lateinit var peerListListener: WifiP2pManager.PeerListListener

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        //if(isInitialStickyBroadcast()) return;

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            p2pManager.requestPeers(channel, peerListListener)
            //      Toast.makeText(context, WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION, Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //        context.setIsWifiP2pEnabled(true);
            } else {
                //        context.setIsWifiP2pEnabled(false);
            }
            //Toast.makeText(context,WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            val networkInfo =
                intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
            if (networkInfo!!.isConnected) {
                //connected
                p2pManager.requestConnectionInfo(channel, infoListener)
            } else {
                //disconnected
            }

            Log.d("BroadCast", "WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION")

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
            val device =
                intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
            thisDeviceName = device!!.deviceName
            //      textView.setText(thisDeviceName);
        }


    }

    fun setPeerListListener(peerListListener: WifiP2pManager.PeerListListener) {
        this.peerListListener = peerListListener
    }

    companion object {

        var thisDeviceName = ""
    }
}