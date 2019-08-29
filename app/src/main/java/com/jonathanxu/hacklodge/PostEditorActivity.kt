package com.jonathanxu.hacklodge

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.onegravity.rteditor.RTEditText
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.RTToolbar
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.api.format.RTFormat
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_post_editor.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import java.time.format.DateTimeFormatter



class PostEditorActivity : AppCompatActivity() {

    private val TAG = "PostEditor"
    private lateinit var fileName: String
    private lateinit var titleDirectory: JSONObject
    private val client = OkHttpClient()

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
        rtManager.registerToolbar(toolbarContainer, rtToolbar as RTToolbar)

        // register editor & set text
        val rtEditText = rtEditText as RTEditText
        rtManager.registerEditor(rtEditText, true)
        Log.d(TAG, "Initialized rtEditor")
        //rtEditText.setRichTextEditing(true, message)
        //Todo: Add back button

        // Location init
        search_location.setOnClickListener{
            val locationInput = et_location.text.toString()
            Log.d(TAG, "Searching: $locationInput")
            val request = Request.Builder()
                //.url("http://photon.komoot.de/api/?q=$locationInput&limit=6")
                .url("https://nominatim.openstreetmap.org/search/$locationInput?format=json&limit=6")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(request: Request?, e: IOException?) {
                }
                override fun onResponse(response: Response) {
                    val jsonResponse = response.body().string()
                    //Log.d(TAG, jsonResponse)

                    val jsonArray = JSONArray(jsonResponse)
                    //Log.d(TAG, jsonArray.javaClass.name)
                    for(i in 0 until jsonArray.length()){
                        val location = jsonArray.getJSONObject(i)
                        val locationName = location.get("display_name").toString()
                        val lat = location.get("lat").toString()
                        val lon = location.get("lon").toString()
                        Log.d(TAG, locationName)
                    }
                    /*val keys = jsonObject.keys()
                    while(keys.hasNext()) {
                        val key = keys.next()
                        val result = jsonObject.get(key)
                        Log.d(TAG, result.toString())
                    }*/
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePost() {
        // Todo: Support more data fields
        // Get the data from the editor
        val title = et_title.text.toString().trim()
        val author = "placeholder"
        val subtitle = et_subtitle.text.toString().trim()
        val location = et_location.text.toString().trim()
        val timestamp = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        val content = rtEditText.getText(RTFormat.HTML)

        // Save the post by writing to file
        Log.d(TAG, "Writing file to private storage")
        Log.d(TAG, timestamp)
        val file = File(filesDir, "$fileName.html")
        // Write the file
        /*
        val metadata = "<meta> author='$author' title ='$title' subtitle='$subtitle' location='$location'" +
                " timestamp='$timestamp'+ </meta>"
        val output = content.substringBefore("<head>") + "<head>" + metadata + content.substringAfter("<head>")
         */
        file.writeText(title + "\n" + author + "\n" + subtitle  + "\n" + location  + "\n" +
        timestamp + "\n" + content)
        // Notify the user the file was saved
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "File saved to ${file.absolutePath}")

        // Go back to main screen
        val intent = Intent(this@PostEditorActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_post, menu)// Menu Resource, Menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.button_save_file -> {
                savePost()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
