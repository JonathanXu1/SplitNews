package com.jonathanxu.hacklodge.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.jonathanxu.hacklodge.ui.wifiDirect.WifiDirectFragment

class WifiDirectBroadcastReceiver(
    private var manager: WifiP2pManager,
    private var channel: WifiP2pManager.Channel,
    private var wifiDirectFragment: WifiDirectFragment
) : BroadcastReceiver() {

    private val TAG = "BroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION")
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                wifiDirectFragment.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION")
                // The peer list has changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.d(TAG, "WIFI_P2P_CONNECTION CHANGE ACTION")
                // Connection state changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION")
                // todo
//                (wifiDirectFragment.activity?.supportFragmentManager?.findFragmentById(R.id.id_goes_here) as DeviceListFragment)
//                    .apply {
//                        updateThisDevice(
//                            intent.getParcelableExtra(
//                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
//                            ) as WifiP2pDevice
//                        )
//                    }
            }
        }
    }
}