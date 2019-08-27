package com.jonathanxu.hacklodge.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.FileListAdapter
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.File

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private val TAG = "Gallery"
    private lateinit var fileList: Array<File>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FileListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileList = activity?.filesDir?.listFiles() as Array<File>

        linearLayoutManager = LinearLayoutManager(this.context)
        gallery_list.layoutManager = linearLayoutManager
        adapter = FileListAdapter(fileList)
        gallery_list.adapter = adapter
        // adapter.notifyItemInserted() might be useful later

        for (file in fileList) {
            Log.d(TAG, "Found file ${file.absolutePath}")
            val newTextView = TextView(this.context)
            newTextView.text = file.name
        }
    }
}