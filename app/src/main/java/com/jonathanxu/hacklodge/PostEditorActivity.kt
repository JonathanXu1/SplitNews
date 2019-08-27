package com.jonathanxu.hacklodge

import android.content.Intent
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
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.util.*


class PostEditorActivity : AppCompatActivity() {

    private val TAG = "PostEditor"
    private lateinit var fileName: String
    private lateinit var titleDirectory: JSONObject

    private fun savePost(view: View) {

        // Get the data from the editor
        val title = et_title.text.toString().trim()
        val location = et_location.text.toString().trim()
        val content = rtEditText.getText(RTFormat.HTML)

        // Get the JSON file title directory
        Log.d(TAG, "Getting title directory JSON")
        val directoryFile = File(filesDir, "titleDirectory.json")
        // Check if directory exists, otherwise just name a new object
        titleDirectory = if (directoryFile.name in fileList()) {
            JSONTokener(directoryFile.readText()).nextValue() as JSONObject
        } else {
            JSONObject()
        }
        // Put the title into the directory
        titleDirectory.put(fileName, title)
        // Write the directory file
        directoryFile.writeText(titleDirectory.toString())
        Log.d(TAG, "Wrote title to directory")

        // Save the post by writing to file
        Log.d(TAG, "Writing file to private storage")
        val file = File(filesDir, "$fileName.html")
        // Write the file
        file.writeText(content)
        // Notify the user the file was saved
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "File saved to ${file.absolutePath}")

        // Go back
        val intent = Intent(this@PostEditorActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set theme before calling setContentView!
        setTheme(R.style.RTE_ThemeLight)

        setContentView(R.layout.activity_post_editor)
        title = "Post an Article"

        // Setup the file we're editing
        val uuidString = UUID.randomUUID().toString()
        fileName = uuidString

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
