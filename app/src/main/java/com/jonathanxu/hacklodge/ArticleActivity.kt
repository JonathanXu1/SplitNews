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




class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_view)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val extras = intent.extras
        if (extras != null) {
            // Load metadata
            val file = extras.get("file") as File
            val bufferedReader = file.bufferedReader()
            val list : List<String> = bufferedReader
                .useLines { lines: Sequence<String> ->
                    lines
                        .take(6)
                        .toList()
                }
            toolbar_layout.title = list[0]
            // Display file
            val textBox = findViewById<WebView>(R.id.news_Body)
            textBox.webViewClient = WebViewClient()
            textBox.loadUrl("file:///" + file.absolutePath)
        }

    }
}
