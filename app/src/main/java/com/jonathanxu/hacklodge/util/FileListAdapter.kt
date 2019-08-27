package com.jonathanxu.hacklodge.util

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonathanxu.hacklodge.R
import kotlinx.android.synthetic.main.news_item_row.view.*
import java.io.File

class FileListAdapter(private val files: Array<File>) : RecyclerView.Adapter<FileListAdapter.FileHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val inflatedView = parent.inflate(R.layout.news_item_row, false)
        return FileHolder(inflatedView)
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val file = files[position]
        holder.bindFile(file)
    }

    class FileHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private val TAG = "FileHolder"
        private lateinit var file: File
        private lateinit var view: View

        init {
            view.setOnClickListener(this)
            this.view = view
        }

        fun bindFile(file: File) {
            this.file = file
            view.item_news_title.text = file.name
        }

        override fun onClick(view: View) {
            Log.d(TAG, "File ${file.name} selected")
            // Todo Start new intent to read the file
            // itemView.context.startActivity(readFileIntent)
        }



    }

}