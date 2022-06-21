package com.example.sit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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

        val text01 = findViewById<TextView>(R.id.sample01)
        val text02 = findViewById<TextView>(R.id.sample02)
        val text03 = findViewById<TextView>(R.id.sample03)

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
            text01.text = if (result.get("1") == "Occupied") {"×"}else{"〇"}
            text02.text = if (result.get("2") == "Occupied") {"×"}else{"〇"}
            text03.text = if (result.get("3") == "Occupied") {"×"}else{"〇"}
        }

        lifecycleScope.launch{
            val result = readBackground()
            showBackground(result)
        }

        //Home
        val home = findViewById<Button>(R.id.home)
        home.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        //Zone
        val zone = findViewById<Button>(R.id.zone)
        zone.setOnClickListener{
            val i = Intent(this, ZoneActivity::class.java)
            startActivity(i)
        }

        //Reload
        val reload = findViewById<Button>(R.id.reload)
        reload.setOnClickListener{
            lifecycleScope.launch{
                val result = readBackground()
                showBackground(result)
            }
        }
    }
}