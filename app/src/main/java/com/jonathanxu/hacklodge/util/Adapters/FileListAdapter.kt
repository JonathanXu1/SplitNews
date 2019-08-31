package com.jonathanxu.hacklodge.util.Adapters

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jonathanxu.hacklodge.ArticleActivity
import com.jonathanxu.hacklodge.R
import com.jonathanxu.hacklodge.util.inflate
import kotlinx.android.synthetic.main.news_item_row.view.*
import org.jsoup.Jsoup
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

            val doc = Jsoup.parse(file, null)
            val metaTags = doc.getElementsByTag("meta")
            for (metaTag in metaTags) {
                val name = metaTag.attr("name")
                val content = metaTag.attr("content")

                when(name){
                    "title" -> view.item_news_title.text = content
                    "subtitle" -> view.item_news_subtitle.text = content
                    "timestamp" -> view.item_news_date.text = content
                }
            }

/*            val bufferedReader = file.bufferedReader()
            val list : List<String> = bufferedReader
                .useLines { lines: Sequence<String> ->
                    lines
                        .take(6)
                        .toList()
                }
            Log.d(TAG, list.toString())
            view.item_news_title.text = list[0]
            view.item_news_subtitle.text = list[2]
            view.item_news_date.text = list[4]*/
        }

        override fun onClick(view: View) {
            Log.d(TAG, "File ${file.name} selected")
            val context = view.context
            val intent = Intent(context,  ArticleActivity::class.java)
            intent.putExtra("file", file)
            context.startActivity(intent)
        }



    }

}