package com.jonathanxu.hacklodge.util

import android.net.wifi.p2p.WifiP2pDevice
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonathanxu.hacklodge.R
import kotlinx.android.synthetic.main.device_item.view.*

class DeviceListAdapter(private val devices: Array<WifiP2pDevice>) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceHolder>() {

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
    }

    class DeviceHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val TAG = "DeviceHolder"
        private lateinit var device: WifiP2pDevice
        private var view: View;

        init {
            view.setOnClickListener(this)
            this.view = view
        }

        fun bindDevice(device: WifiP2pDevice) {
            this.device = device
            view.device_name.text = device.deviceName
        }

        override fun onClick(clickedView: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}