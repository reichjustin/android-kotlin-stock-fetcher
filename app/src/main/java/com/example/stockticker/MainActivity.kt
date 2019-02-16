package com.example.stockticker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.View
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fetchQuoteButton: Button = findViewById(R.id.get_quote)
        val priceView: TextView = findViewById(R.id.price)
        val stockText: TextView = findViewById(R.id.stock_ticker)
        setupHandlers(priceView,stockText, fetchQuoteButton)
    }

    fun setupHandlers(priceView: TextView, stockText: TextView, fetchQuoteButton: Button) {
        fetchQuoteButton.setOnClickListener {
            val ticker = stockText.text
            if (ticker === null || ticker.length === 0) {
                priceView.visibility = View.VISIBLE
                priceView.text = "No ticker provided"
            } else {
                val job = SupervisorJob()
                GlobalScope.launch(job + Dispatchers.Main) {
                    val response = async(Dispatchers.IO) {
                        val b: Deferred<Response> = async {
                            val request = Request
                                .Builder()
                                .url("http://10.0.2.2:8080/price/$ticker")
                                .build()

                            httpClient.newCall(request).execute()

                        }

                        b.await().body()?.string()
                    }

                    priceView.visibility = View.VISIBLE

                    val parsedJson: JSONObject? = JSONObject(response.await())

                    if (parsedJson !== null) {
                       priceView.text = parsedJson.getString("close")
                    }
                }
            }
        }
    }
}
