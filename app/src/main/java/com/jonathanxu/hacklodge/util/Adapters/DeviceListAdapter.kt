package com.jonathanxu.hacklodge.util.Adapters

import android.net.wifi.p2p.WifiP2pDevice
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.DeviceOutputSocket
import com.jonathanxu.hacklodge.util.inflate
import kotlinx.android.synthetic.main.device_item.view.*

class DeviceListAdapter(private val devices: MutableList<WifiP2pDevice>) :
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
        holder.bindDevice(device)
        Log.d(
            TAG,
            "${device.deviceName} bound to view. Device ${position + 1} out of ${devices.size}"
        )
    }

    class DeviceHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val TAG = "DeviceHolder"
        private lateinit var device: WifiP2pDevice
        private var view: View

        init {
            view.setOnClickListener(this)
            this.view = view
        }

        fun bindDevice(device: WifiP2pDevice) {
            this.device = device
            view.device_name.text = device.deviceName
            Log.d(TAG, "${device.deviceName} bound to view.")
        }

        override fun onClick(clickedView: View?) {
            Log.d(TAG, "${clickedView?.device_name?.text} selected")
            DeviceOutputSocket(device.deviceAddress).execute()
//            val config = WifiP2pConfig().apply {
//                deviceAddress = device.deviceAddress
//                wps.setup = WpsInfo.PBC
//            }
        }

    }

}