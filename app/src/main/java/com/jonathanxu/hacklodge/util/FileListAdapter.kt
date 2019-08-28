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
        private var view: View

        init {
            view.setOnClickListener(this)
            this.view = view
        }

        fun bindFile(file: File) {
            this.file = file
            /*
            val doc = Jsoup.parse(file.absolutePath)
            val metaTags = doc.getElementsByTag("meta")
            for (metaTag in metaTags) {
                var author = metaTag.attr("author")
                var subtitle = metaTag.attr("subtitle")
                var location = metaTag.attr("location")
                var timestamp  = metaTag.attr("timestamp")
                view.item_news_title.text = metaTag.attr("title")
                break
            }
            */
            val bufferedReader = file.bufferedReader()
            val list : List<String> = bufferedReader
                .useLines { lines: Sequence<String> ->
                    lines
                        .take(6)
                        .toList()
                }
            Log.d(TAG, list.toString())
            view.item_news_title.text = list[0]
            view.item_news_subtitle.text = list[2]
        }

        override fun onClick(view: View) {
            Log.d(TAG, "File ${file.name} selected")
            // Todo Start new intent to read the file
            // itemView.context.startActivity(readFileIntent)
        }



    }

}