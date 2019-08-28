package com.jonathanxu.hacklodge.ui.wifiDirect

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.WifiDirectBroadcastReceiver
import kotlinx.android.synthetic.main.fragment_wifi_direct.*

class WifiDirectFragment : Fragment() {

    private lateinit var wifiDirectViewModel: WifiDirectViewModel
    private val TAG = "WifiDirectFragment"

    // WiFi P2P stuff
    private val intentFilter = IntentFilter()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    var isWifiP2pEnabled = false

    private lateinit var receiver: WifiDirectBroadcastReceiver

    // Todo move into Adapter for RecyclerView
    private val peers = mutableListOf<WifiP2pDevice>()
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            // todo (listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wifiDirectViewModel =
            ViewModelProviders.of(this).get(WifiDirectViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_wifi_direct, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        wifiDirectViewModel.text.observe(this, Observer {
            textView.text = it
        })

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Initializing WifiP2pManager")

        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = this.context?.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this.context, Looper.getMainLooper(), null)

        Log.d(TAG, "WifiP2pManager initialized")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_direct_scan.text = getString(R.string.scan_direct)
        button_direct_scan.setOnClickListener { clickedView -> startPeerDiscovery(clickedView) }
    }

    override fun onResume() {
        super.onResume()
        receiver = WifiDirectBroadcastReceiver(manager, channel, peerListListener, this)
        this.context?.registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        this.context?.unregisterReceiver(receiver)
    }

    private fun startPeerDiscovery(view: View) {

        // Make sure we have the location permission and ask for it if we don't
        var locationPermissionStatus = ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0)
        }

        // Disable the button
        (view as Button).text = getString(R.string.scan_progress)
        view.isEnabled = false

        // Start peer discovery
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
                Log.d(TAG, "Peer discovery successfully initiated")
            }

            override fun onFailure(reasonCode: Int) {
                // Code for when the discovery initiation fails goes here.
                Log.d(TAG, "Peer discovery failed with error code $reasonCode")

                // Alert the user that something went wrong.
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                (view as Button).text = getString(R.string.scan_direct)
                view.isEnabled = true
            }
        })

    }

}