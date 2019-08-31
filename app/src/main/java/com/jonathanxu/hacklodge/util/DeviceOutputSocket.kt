package com.jonathanxu.hacklodge.util

import android.os.AsyncTask
import android.util.Log
import java.net.InetSocketAddress
import java.net.Socket

class DeviceOutputSocket(private var serverAddress: String) : AsyncTask<Void, Void, String>(){

    private val TAG = "DeviceOutputSocket"
    private val port = 42069

    init {
        Log.d(TAG, "New async task created")
    }

    override fun doInBackground(vararg args: Void?): String? {
        val socket = Socket()
        socket.bind(null)

        Log.d(TAG, "Socket Address ${socket.localSocketAddress}")

        socket.connect(InetSocketAddress(serverAddress, port))
        Log.d(TAG, "Connected to $serverAddress:$port")

        // Write whatever idk
        val outputStream = socket.getOutputStream()
        outputStream.write("Yeet".toByteArray())
        Log.d(TAG, "Wrote to socket")

        // Close the output stream and socket
        outputStream.flush()
        outputStream.close()
        socket.close()
        return null
    }
}