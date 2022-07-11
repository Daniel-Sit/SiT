package com.example.sit

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val map = findViewById<TextView>(R.id.cafeteriamapText)
        val light = findViewById<ImageView>(R.id.light)
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
            val percent = Math.round(result.count{it.value == "Vacant"}.toDouble()/result.count().toDouble()*100).toInt()
            if(percent >= 40){
                light.setImageResource(R.drawable.greenlight)
            }else if(percent > 0){
                light.setImageResource(R.drawable.yellowlight)
            }else{
                light.setImageResource(R.drawable.redlight)
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

        //Zone
        val zone = findViewById<ImageButton>(R.id.zone)
        zone.setOnClickListener{
            val i = Intent(this, ZoneActivity::class.java)
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