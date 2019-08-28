package com.jonathanxu.hacklodge.ui.torrent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jonathanxu.hacklodge.R

class TorrentFragment : Fragment() {

    private lateinit var torrentViewModel: TorrentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        torrentViewModel =
            ViewModelProviders.of(this).get(TorrentViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_torrent, container, false)
        val textView: TextView = root.findViewById(R.id.text_send)
        torrentViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}