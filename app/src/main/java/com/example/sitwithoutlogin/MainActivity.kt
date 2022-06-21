package com.example.sitwithoutlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text1 = findViewById<TextView>(R.id.homeText01)

        @WorkerThread
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
            text1.text = result.count{it.value == "Vacant"}.toString()+"/"+result.count().toString()
        }

        lifecycleScope.launch{
            val result = readBackground()
            showBackground(result)
        }

        //Table
        val table = findViewById<Button>(R.id.table)
        table.setOnClickListener {
            val i = Intent(this, ZoneActivity::class.java)
            startActivity(i)
        }
    }
}