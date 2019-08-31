package com.jonathanxu.hacklodge

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_article_view.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import java.io.File
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.news_item_row.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_view)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        lateinit var doc : Document
        if (extras != null) {
            // Load metadata
            val file = extras.get("file") as File

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
                }
            }

            // Display file
            val textBox = findViewById<WebView>(R.id.news_Body)
            textBox.webViewClient = WebViewClient()
            textBox.loadUrl("file:///" + file.absolutePath)
        }

        // Fab listeners

        uv_btn.setOnClickListener { view ->
            // Click, but already selected
            if(uv_btn.tag.toString().trim() == "on"){
                uv_btn.tag = "off"
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                Toast.makeText(this, "Removed vote", Toast.LENGTH_SHORT).show()
                // Down 1 point
                doc.select("meta[content=userVote]").attr("content", "0")

            } else {
                uv_btn.tag = "on"
                dv_btn.tag = "off"
                Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show()
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                doc.select("meta[content=userVote]").attr("content", "1")
                if(dv_btn.tag.toString().trim() == "on"){
                    // Up 2 points
                } else {
                    // Up 1 point
                }
            }
        }
        dv_btn.setOnClickListener { view ->
            if(dv_btn.tag.toString().trim() == "on"){
                dv_btn.tag = "off"
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                Toast.makeText(this, "Removed vote", Toast.LENGTH_SHORT).show()
                // Up 1 point
                doc.select("meta[content=userVote]").attr("content", "0")
            } else {
                dv_btn.tag = "on"
                uv_btn.tag = "off"
                Toast.makeText(this, "Downvoted!", Toast.LENGTH_SHORT).show()
                dv_btn.backgroundTintList = this.resources.getColorStateList(R.color.colorAccent)
                uv_btn.backgroundTintList = this.resources.getColorStateList(R.color.fabOff)
                doc.select("meta[content=userVote]").attr("content", "-1")
                if(uv_btn.tag.toString().trim() == "on"){
                    // Down 2 points
                } else {
                    // Down 1 point
                }
            }
        }

    }
}
