package com.jonathanxu.hacklodge.util

import android.os.AsyncTask
import android.util.Log
import java.net.ServerSocket

class InputAsyncTask: AsyncTask<Void, Void, String>() {

    private val TAG = "InputAsyncTask"
    private val port = 42069

    override fun doInBackground(vararg args: Void?): String? {
        val serverSocket = ServerSocket(port)
        Log.d(TAG, "Socket opened on port $port")

        val socket = serverSocket.accept()

        val input = socket.getInputStream().read()

        Log.d(TAG, "Read from input: $input")

        socket.close()
        serverSocket.close()

        return null
    }
}