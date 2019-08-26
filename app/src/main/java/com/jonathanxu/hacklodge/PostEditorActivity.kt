package com.jonathanxu.hacklodge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_post_editor.*
import java.io.File


class PostEditorActivity : AppCompatActivity() {

    private fun savePost(view: View) {
        // Save the post by writing to file
        Log.d("SAVE", "Writing file to private storage")
        // Todo post name
        val file = File(filesDir, "post_hard.md")
        file.writeText("This a file")
        Log.d("SAVE", "File written to ${file.absolutePath}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_editor)
        title = "Post an Article"

        //Todo: Add back button

        button_save_file.setOnClickListener { view -> savePost(view)}
    }
}
