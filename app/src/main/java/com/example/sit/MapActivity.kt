package com.example.sit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val map = findViewById<TextView>(R.id.cafeteriamapText)

        suspend fun readBackground():MutableMap<String,String>{
            return withContext(Dispatchers.IO){
                var seatData = mutableMapOf<String,String>("1" to "Occupied","2" to "Occupied","3" to "Occupied")
                val result = StringBuilder()
                val url = URL("http://10.0.2.2:8000/api/status")
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                val reader = con.inputStream.bufferedReader()
                reader.forEachLine {
                    result.append(it)
                }

                val json = JSONObject("{\"status\":"+result.toString()+"}")
                val status = json.getJSONArray("status")
                for (i in 0 until status.length()) {
                    val data = status.getJSONObject(i)
                    val seatNumber = data.getString("id")
                    val seatStatus = data.getString("seat")
                    seatData[seatNumber] = seatStatus
                }
                seatData
            }
        }

        @UiThread
        fun showBackground(result: MutableMap<String,String>){
            map.text = result.count{it.value == "Vacant"}.toString()+"/"+result.count().toString()
        }

        lifecycleScope.launch{
            val result = readBackground()
            showBackground(result)
        }

        //Home
        val home = findViewById<ImageButton>(R.id.home)
        home.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        //Zone
        val zone = findViewById<ImageButton>(R.id.zone)
        zone.setOnClickListener{
            val i = Intent(this, ZoneActivity::class.java)
            startActivity(i)
        }

        /*
        //Reload
        val reload = findViewById<Button>(R.id.reload)
        reload.setOnClickListener{
            lifecycleScope.launch{
                val result = readBackground()
                //showBackground(result)
            }
        }
        */
    }
}