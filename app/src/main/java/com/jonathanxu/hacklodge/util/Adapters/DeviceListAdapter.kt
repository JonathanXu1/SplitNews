package com.jonathanxu.hacklodge.util.Adapters

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.inflate
import kotlinx.android.synthetic.main.device_item.view.*

class DeviceListAdapter(
    private val devices: MutableList<WifiP2pDevice>,
    private val channel: WifiP2pManager.Channel,
    private val manager: WifiP2pManager
) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceHolder>() {

    private val TAG = "DeviceListAdapter"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceHolder {
        val inflatedView = parent.inflate(R.layout.device_item, false)
        return DeviceHolder(inflatedView)
    }

    override fun getItemCount() = devices.size

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        val device = devices[position]
        holder.bindDevice(device, channel, manager)
        Log.d(
            TAG,
            "${device.deviceName} bound to view. Device ${position + 1} out of ${devices.size}"
        )
    }

    class DeviceHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val TAG = "DeviceHolder"
        private var view: View
        private lateinit var device: WifiP2pDevice
        private lateinit var channel: WifiP2pManager.Channel
        private lateinit var manager: WifiP2pManager

        private val port = 42069

        init {
            view.setOnClickListener(this)
            this.view = view

        }

        fun bindDevice(
            device: WifiP2pDevice,
            channel: WifiP2pManager.Channel,
            manager: WifiP2pManager
        ) {
            this.device = device
            this.channel = channel
            this.manager = manager
            view.device_name.text = device.deviceName
            Log.d(TAG, "${device.deviceName} bound to view.")
        }

        override fun onClick(clickedView: View?) {
            Log.d(TAG, "${clickedView?.device_name?.text} selected")

            val config = WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
                wps.setup = WpsInfo.PBC
            }

            manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Successfully connected to ${device.deviceName}")
                    val deviceAddr = device.deviceAddress
                    Log.d(TAG, "Device address is $deviceAddr")

                    manager.requestConnectionInfo(channel
                    ) { info -> Log.d(TAG, "Connection info: $info") }

                }

                override fun onFailure(reasonCode: Int) {
                    Log.d(
                        TAG,
                        "Failed to connect to ${device.deviceName} with code $reasonCode"
                    )
                }
            })
//            DeviceOutputSocket(device.deviceAddress).execute()
        }

    }

}