package com.jonathanxu.hacklodge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_post_editor.*
import android.util.Log
import android.view.View
import android.widget.Toast
import java.io.File
import java.util.*
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.api.RTApi
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class PostEditorActivity : AppCompatActivity() {

    private fun savePost(view: View) {
        // Save the post by writing to file
        Log.d("SAVE", "Writing file to private storage")
        // Get the data from the editor
        // Todo the actual post body
        val title = et_title.text.toString().trim()
        et_title.text.clear()
        val location = et_location.text.toString().trim()
        et_location.text.clear()
        // Make file title lower_case
        val fileTitle = title.replace(" ", "_").toLowerCase(Locale.ROOT)
        val file = File(filesDir, "$fileTitle.md")
        // Write the file todo construct the string before passing to writeText
        // Todo consider whether to include location and such as metadata
        file.writeText("# $title\nThis file that came from $location")
        // Notify the user the file was saved
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        Log.d("SAVE", "File written to ${file.absolutePath}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set theme before calling setContentView!
        setTheme(R.style.RTE_ThemeLight)

        setContentView(R.layout.activity_post_editor)
        title = "Post an Article"

        // create RTManager
        val rtApi = RTApi(this, RTProxyImpl(this), RTMediaFactoryImpl(this, true))
        val rtManager = RTManager(rtApi, savedInstanceState)




        //Todo: Add back button

        button_save_file.setOnClickListener { view -> savePost(view) }

    }
}
