package com.jonathanxu.hacklodge.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.FileListAdapter
import kotlinx.android.synthetic.main.fragment_feed.*
import java.io.File

class FeedFragment : Fragment() {

    private lateinit var feedViewModel: FeedViewModel
    private val TAG = "Gallery"
    private lateinit var fileList: Array<File>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FileListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        feedViewModel =
            ViewModelProviders.of(this).get(FeedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_feed, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileList = activity?.filesDir?.listFiles() as Array<File>
        linearLayoutManager = LinearLayoutManager(this.context)

        if (fileList.isEmpty()){
            noNews.visibility = View.VISIBLE
        } else {
            noNews.visibility = View.GONE
            gallery_list.layoutManager = linearLayoutManager
            adapter = FileListAdapter(fileList)
            gallery_list.adapter = adapter
            // adapter.notifyItemInserted() might be useful later
        }

    }
}