package com.example.sit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ZoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zone)

        val east = findViewById<TextView>(R.id.eastText)
        val west = findViewById<TextView>(R.id.westText)
        val light1 = findViewById<ImageView>(R.id.light1)
        val light2 = findViewById<ImageView>(R.id.light2)
        val chair1 = findViewById<ImageView>(R.id.chair1)
        val chair3 = findViewById<ImageView>(R.id.chair3)
        val chair5 = findViewById<ImageView>(R.id.chair5)
        val chair11 = findViewById<ImageView>(R.id.chair11)
        val chair13 = findViewById<ImageView>(R.id.chair13)
        val chair23 = findViewById<ImageView>(R.id.chair23)
        val chair25 = findViewById<ImageView>(R.id.chair25)
        val chair27 = findViewById<ImageView>(R.id.chair27)
        val chair33 = findViewById<ImageView>(R.id.chair33)
        val chair35 = findViewById<ImageView>(R.id.chair35)

        suspend fun readBackground():MutableMap<String,String>{
            return withContext(Dispatchers.IO){
                var seatData = mutableMapOf<String,String>("1" to "Occupied","2" to "Occupied","3" to "Occupied","4" to "Occupied","5" to "Occupied","6" to "Occupied","7" to "Occupied","8" to "Occupied","9" to "Occupied","10" to "Occupied")
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
            east.text = result.count{it.key.toInt() <= 5 && it.value == "Vacant"}.toString()+"/"+result.count{it.key.toInt() <= 5}.toString()
            west.text = result.count{it.key.toInt() > 5 && it.value == "Vacant"}.toString()+"/"+result.count{it.key.toInt() > 5}.toString()
            val percent1 = Math.round(result.count{it.key.toInt() <= 5 && it.value == "Vacant"}.toDouble()/result.count{it.key.toInt() <= 5}.toDouble()*100).toInt()
            val percent2 = Math.round(result.count{it.key.toInt() > 5 && it.value == "Vacant"}.toDouble()/result.count{it.key.toInt() > 5}.toDouble()*100).toInt()
            if(percent1 >= 40){
                light1.setImageResource(R.drawable.greenlight)
            }else if(percent1 > 0){
                light1.setImageResource(R.drawable.yellowlight)
            }else{
                light1.setImageResource(R.drawable.redlight)
            }
            if(percent2 >= 40){
                light2.setImageResource(R.drawable.greenlight)
            }else if(percent2 > 0){
                light2.setImageResource(R.drawable.yellowlight)
            }else{
                light2.setImageResource(R.drawable.redlight)
            }
            if(result["1"] == "Vacant"){
                chair1.setImageResource(R.drawable.freeseat)
            }else{
                chair1.setImageResource(R.drawable.occupiedseat)
            }
            if(result["2"] == "Vacant"){
                chair3.setImageResource(R.drawable.freeseat)
            }else{
                chair3.setImageResource(R.drawable.occupiedseat)
            }
            if(result["3"] == "Vacant"){
                chair5.setImageResource(R.drawable.freeseat)
            }else{
                chair5.setImageResource(R.drawable.occupiedseat)
            }
            if(result["4"] == "Vacant"){
                chair11.setImageResource(R.drawable.freeseat)
            }else{
                chair11.setImageResource(R.drawable.occupiedseat)
            }
            if(result["5"] == "Vacant"){
                chair13.setImageResource(R.drawable.freeseat)
            }else{
                chair13.setImageResource(R.drawable.occupiedseat)
            }
            if(result["6"] == "Vacant"){
                chair23.setImageResource(R.drawable.freeseat)
            }else{
                chair23.setImageResource(R.drawable.occupiedseat)
            }
            if(result["7"] == "Vacant"){
                chair25.setImageResource(R.drawable.freeseat)
            }else{
                chair25.setImageResource(R.drawable.occupiedseat)
            }
            if(result["8"] == "Vacant"){
                chair27.setImageResource(R.drawable.freeseat)
            }else{
                chair27.setImageResource(R.drawable.occupiedseat)
            }
            if(result["9"] == "Vacant"){
                chair33.setImageResource(R.drawable.freeseat)
            }else{
                chair33.setImageResource(R.drawable.occupiedseat)
            }
            if(result["10"] == "Vacant"){
                chair35.setImageResource(R.drawable.freeseat)
            }else{
                chair35.setImageResource(R.drawable.occupiedseat)
            }
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
            finish()
        }

        //Map
        val map = findViewById<ImageButton>(R.id.map)
        map.setOnClickListener{
            val i = Intent(this, MapActivity::class.java)
            startActivity(i)
            finish()
        }

        //Reload
        val reload = findViewById<ImageButton>(R.id.reload)
        reload.setOnClickListener{
            lifecycleScope.launch{
                val result = readBackground()
                showBackground(result)
            }
        }
    }
}