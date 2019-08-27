package com.jonathanxu.hacklodge

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.onegravity.rteditor.RTEditText
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.RTToolbar
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.api.format.RTFormat
import kotlinx.android.synthetic.main.activity_post_editor.*
import java.io.File
import java.util.*


class PostEditorActivity : AppCompatActivity() {

    private val TAG = "PostEditor"

    private lateinit var fileName: String

    private fun savePost(view: View) {
        // Save the post by writing to file
        Log.d(TAG, "Writing file to private storage")
        // Get the data from the editor
        // Todo the actual post body
        val title = et_title.text.toString().trim()
        val location = et_location.text.toString().trim()

        val content = rtEditText.getText(RTFormat.HTML)

        val file = File(filesDir, fileName)

        // Write the file
        val outputBuilder = StringBuilder()
        outputBuilder.append("# $title\n")
        outputBuilder.append(content)
        // Todo consider whether to include location and such as metadata
        file.writeText(outputBuilder.toString())
        // Notify the user the file was saved
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "File saved to ${file.absolutePath}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set theme before calling setContentView!
        setTheme(R.style.RTE_ThemeLight)

        setContentView(R.layout.activity_post_editor)
        title = "Post an Article"

        // Setup the file we're editing/
        // Todo take file names from the intent if it already exists

        fileName = UUID.randomUUID().toString() + ".md"

        // create RTManager
        val rtApi = RTApi(this, RTProxyImpl(this), RTMediaFactoryImpl(this, true))
        val rtManager = RTManager(rtApi, savedInstanceState)
        // register toolbar
        val toolbarContainer = rte_toolbar_container as ViewGroup
        val rtToolbar: View = toolbarContainer.findViewById(R.id.rte_toolbar)
        if (rtToolbar != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar as RTToolbar)
        }

        // register editor & set text
        val rtEditText = rtEditText as RTEditText
        rtManager.registerEditor(rtEditText, true)
        Log.d(TAG, "Initialized rtEditor")
        //rtEditText.setRichTextEditing(true, message)
        //Todo: Add back button

        button_save_file.setOnClickListener { view -> savePost(view) }

    }
}
