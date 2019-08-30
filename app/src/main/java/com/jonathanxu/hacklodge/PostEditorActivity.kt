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
import android.widget.ArrayAdapter
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
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.activity_post_editor.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class PostEditorActivity : AppCompatActivity() {

    private val TAG = "PostEditor"
    private val context = this
    private lateinit var fileName: String
    private lateinit var titleDirectory: JSONObject
    private val client = OkHttpClient()

    private var mTimer = Timer()
    private var mList = ArrayList<String>()
    private lateinit var mAdapter:ArrayAdapter<String>

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

        // Location search dropdown

        // Get the string array
        val countries: Array<out String> = resources.getStringArray(R.array.locations_array)
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries).also { adapter ->
            et_location.setAdapter(adapter)
        }

        // Location listener
        et_location.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                if (!input.isNullOrBlank()) {
                    getLocationList(input.toString())
                }
            }
            override fun beforeTextChanged(inputText: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mTimer.cancel()
//                if (!inputText.isNullOrBlank()) {
//                    getLocationList(inputText.toString())
//                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mTimer.cancel()
//                if (et_location.text.toString().isNotBlank()){
//                    getLocationList(et_location.text.toString())
//                    mTimer = Timer()
//                    mTimer.schedule(
//                        object: TimerTask() {
//                            override fun run() {
//                                getLocationList(et_location.text.toString())
//                            }
//                        },
//                        1000
//                    )
//                }
            }

        })

    }

    fun getLocationList(locationInput:String) {
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
                mList.clear()
                for(i in 0 until jsonArray.length()){
                    val location = jsonArray.getJSONObject(i)
                    val locationName = location.get("display_name").toString()
                    val lat = location.get("lat").toString()
                    val lon = location.get("lon").toString()
                    //Log.d(TAG, locationName)
                    mList.add(locationName)
                }
                Log.d(TAG, "Data set changed, now has ${mList.size} items")
                runOnUiThread {
                    mAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, mList)
                    et_location.threshold = 1
                    et_location.setAdapter(mAdapter)
                    mAdapter.notifyDataSetChanged()
                }
            }
        })
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
        val file = File(filesDir, "$fileName.html")
        // Write the file
        /*
        val metadata = "<meta> author='$author' title ='$title' subtitle='$subtitle' location='$location'" +
                " timestamp='$timestamp'+ </meta>"
        val output = content.substringBefore("<head>") + "<head>" + metadata + content.substringAfter("<head>")
         */
        file.appendText("<meta name=\"title\" content=\"$title\"/> \n")
        file.appendText("<meta name=\"author\" content=\"$author\"/> \n")
        file.appendText("<meta name=\"subtitle\" content=\"$subtitle\"/> \n")
        file.appendText("<meta name=\"location\" content=\"$location\"/> \n")
        file.appendText("<meta name=\"timestamp\" content=\"$timestamp\"/> \n")
        file.appendText(content)

        /*file.writeText(title + "\n" + author + "\n" + subtitle  + "\n" + location  + "\n" +
        timestamp + "\n" + content)*/

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

    @RequiresApi(Build.VERSION_CODES.O)
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
