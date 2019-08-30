package com.jonathanxu.hacklodge

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_article_view.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.webkit.WebView
import android.widget.TextView
import java.io.File
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.news_item_row.view.*
import org.jsoup.Jsoup


class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_view)
        setSupportActionBar(toolbar)
        uv_btn.setOnClickListener { view ->
            Snackbar.make(view, "Upvote", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        dv_btn.setOnClickListener { view ->
            Snackbar.make(view, "Downvote", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val extras = intent.extras
        if (extras != null) {
            // Load metadata
            val file = extras.get("file") as File

            val doc = Jsoup.parse(file, null)
            val metaTags = doc.getElementsByTag("meta")
            for (metaTag in metaTags) {
                val name = metaTag.attr("name")
                val content = metaTag.attr("content")

                when(name){
                    "title" -> toolbar_layout.title = content
//                    "subtitle" -> view.item_news_subtitle.text = content
//                    "timestamp" -> view.item_news_date.text = content
                }
            }

            // Display file
            val textBox = findViewById<WebView>(R.id.news_Body)
            textBox.webViewClient = WebViewClient()
            textBox.loadUrl("file:///" + file.absolutePath)
        }

    }
}
