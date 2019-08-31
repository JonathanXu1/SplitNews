package com.jonathanxu.hacklodge

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_article_view.*
import android.util.Log
import android.webkit.WebView
import java.io.File
import android.webkit.WebViewClient
import android.widget.Toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.PrintWriter


class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_view)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        lateinit var doc : Document
        lateinit var file: File
        if (extras != null) {
            // Load metadata
            file = extras.get("file") as File

            doc = Jsoup.parse(file, null)
            val metaTags = doc.getElementsByTag("meta")
            for (metaTag in metaTags) {
                val name = metaTag.attr("name")
                val content = metaTag.attr("content")

                when(name){
                    "title" -> toolbar_layout.title = content
                    "subtitle" -> article_subtitle.text = content
                    "timestamp" -> news_time.text = content
                    "author" -> news_author.text = content
                    "location" -> news_location.text = content
                    "userVote" -> {
                        Log.d("cust", content)
                        when(content){
                            "-1" -> {
                                dv_btn.tag = "on"
                                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                            }
                            "1" -> {
                                uv_btn.tag = "on"
                                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                            }
                        }
                    }
                    "votes" -> news_votes.text = content
                }
            }

            // Display file
            val textBox = findViewById<WebView>(R.id.news_Body)
            textBox.webViewClient = WebViewClient()
            textBox.loadUrl("file:///" + file.absolutePath)
        }

        // Fab listeners
        val oldVotes = doc.select("meta[name=votes]").first().attr("content")
        var newVotes:String
        uv_btn.setOnClickListener { view ->
            //Log.d("cust",doc.select("meta[name=userVote]").first().attr("content"))
            // Click, but already selected
            if(uv_btn.tag.toString().trim() == "on"){
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                // Reset vote
                doc.select("meta[name=userVote]").first().attr("content", "0")
                newVotes = oldVotes
                uv_btn.tag = "off"
                Toast.makeText(this, "Removed vote", Toast.LENGTH_SHORT).show()
            } else {
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                doc.select("meta[name=userVote]").first().attr("content", "1")
                // Upvote point
                newVotes = (oldVotes.toInt()+1).toString()
                uv_btn.tag = "on"
                dv_btn.tag = "off"
                Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show()
            }
            //Log.d("cust",doc.select("meta[name=userVote]").first().attr("content"))
            doc.select("meta[name=votes]").first().attr("content", newVotes)
            news_votes.text = newVotes
            file.writeText(doc.toString())
        }
        dv_btn.setOnClickListener { view ->
            if(dv_btn.tag.toString().trim() == "on"){
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                // Reset vote
                doc.select("meta[content=userVote]").attr("content", "0")
                newVotes = oldVotes
                dv_btn.tag = "off"
                Toast.makeText(this, "Removed vote", Toast.LENGTH_SHORT).show()
            } else {
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                doc.select("meta[content=userVote]").attr("content", "-1")
                // Downvote
                newVotes = (oldVotes.toInt()-1).toString()
                dv_btn.tag = "on"
                uv_btn.tag = "off"
                Toast.makeText(this, "Downvoted!", Toast.LENGTH_SHORT).show()
            }
            doc.select("meta[name=votes]").first().attr("content", newVotes)
            news_votes.text = newVotes
            file.writeText(doc.toString())
        }

    }
}
