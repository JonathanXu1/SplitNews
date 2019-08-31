package com.jonathanxu.hacklodge.ui.wifiDirect

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.Adapters.DeviceListAdapter
import com.jonathanxu.hacklodge.util.InputAsyncTask
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
    private val port = 42069

    // For the RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var deviceListAdapter: DeviceListAdapter

    private lateinit var receiver: WifiDirectBroadcastReceiver

    private val buddies = mutableMapOf<String, String>()

    private val peers = mutableListOf<WifiP2pDevice>()
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            deviceListAdapter.notifyDataSetChanged()
            Log.d(TAG, "Peer list changed. Found ${peers.size} peers")

            for (device in peers) {
                val config = WifiP2pConfig().apply {
                    deviceAddress = device.deviceAddress
                    wps.setup = WpsInfo.PBC
                }

//                manager.connect(channel, config, object : WifiP2pManager.ActionListener {
//                    override fun onSuccess() {
//                        Log.d(TAG,"Successfully connected to ${device.deviceName}")
//                        val deviceAddr = device.deviceAddress
//                        Log.d(TAG, "Device address is $deviceAddr")
//                        DeviceOutputSocket(deviceAddr).execute()
//                    }
//
//                    override fun onFailure(reasonCode: Int) {
//                        Log.d(TAG, "Failed to connect to ${device.deviceName} with code $reasonCode")
//                    }
//
//                })
            }

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

        // Make sure we have the location permission and ask for it if we don't
        val locationPermissionStatus = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (locationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

        linearLayoutManager = LinearLayoutManager(this.context)
        device_list.layoutManager = linearLayoutManager
        deviceListAdapter = DeviceListAdapter(peers)
        device_list.adapter = deviceListAdapter

        button_direct_scan.text = getString(R.string.scan_direct)
        // button_direct_scan.setOnClickListener { clickedView -> startPeerDiscovery(clickedView) }
        button_direct_scan.setOnClickListener { clickedView -> discoverService(clickedView) }

        button_direct_broadcast.text = getString(R.string.broadcast_direct)
        button_direct_broadcast.setOnClickListener { clickedView ->
            startServiceRegistration(
                clickedView
            )
        }
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

    private fun startServiceRegistration(view: View) {

        (view as Button).text = getString(R.string.broadcast_progress)
        view.isEnabled = false

        val record: Map<String, String> = mapOf(
            "listenport" to port.toString(),
            "buddyname" to "idk what this is lmao", // todo figure out
            "available" to "visible"
        )
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record)

        manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Service successfully added")
            }

            override fun onFailure(reasonCode: Int) {
                Log.d(TAG, "Service couldn't be added with error code $reasonCode")

                // Alert the user that something went wrong.
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                view.text = getString(R.string.broadcast_direct)
                view.isEnabled = true
            }

        })

        InputAsyncTask().execute()

    }

    private fun startPeerDiscovery(view: View) {

        // Disable the button
        (view as Button).text = getString(R.string.scan_progress)
        view.isEnabled = false

        // Start service discovery
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
                view.text = getString(R.string.scan_direct)
                view.isEnabled = true
            }
        })

    }

    private fun discoverService(view: View) {

        // Disable the button
        (view as Button).text = getString(R.string.scan_progress)
        view.isEnabled = false

        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */
        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
            Log.d(TAG, "DnsSdTxtRecord available -$record")
            record["buddyname"]?.also {
                buddies[device.deviceAddress] = it
            }
        }

        val serviceListener =
            WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->
                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName =
                    buddies[resourceType.deviceAddress] ?: resourceType.deviceName

                // Add to the custom adapter defined specifically for showing
                // wifi devices.
//                val fragment = fragmentManager.findFragmentById(R.id.frag_peerlist) as WiFiDirectServicesList
//                (fragment.listAdapter as WiFiDevicesAdapter).apply {
//                    add(resourceType)
//                    notifyDataSetChanged()
//                }

                Log.d(TAG, "onBonjourServiceAvailable $instanceName")
            }

        manager.setDnsSdResponseListeners(channel, serviceListener, txtListener)

        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.addServiceRequest(
            channel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Successfully added service request. What does it mean? No idea.")
                }

                override fun onFailure(code: Int) {
                    Log.d(TAG, "Failed to add service request with error code $code")

                    // Alert the user that something went wrong.
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    view.text = getString(R.string.broadcast_direct)
                    view.isEnabled = true
                }
            }
        )

        manager.discoverServices(
            channel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Successfully began service discovery")
                }

                override fun onFailure(code: Int) {
                    // Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    when (code) {
                        WifiP2pManager.P2P_UNSUPPORTED -> {
                            Log.d(TAG, "P2P isn't supported on this device.")

                            // Alert the user that something went wrong.
                            Toast.makeText(
                                context,
                                "P2P isn't supported on this device",
                                Toast.LENGTH_SHORT
                            ).show()
                            view.text = getString(R.string.broadcast_direct)
                            view.isEnabled = true
                        }
                        else -> {
                            // Alert the user that something went wrong.
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                            view.text = getString(R.string.broadcast_direct)
                            view.isEnabled = true
                        }
                    }
                }
            }
        )
    }
}