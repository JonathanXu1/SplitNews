package com.jonathanxu.hacklodge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_post_editor.*


class PostEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_editor)
        title = "Post an Article"

        //Todo: Add back button

        // obtain an instance of Markwon
        val markwon = Markwon.create(this)
        // set markdown
        markwon.setMarkdown(editorTextView, "**Hello there!**");
    }
}
