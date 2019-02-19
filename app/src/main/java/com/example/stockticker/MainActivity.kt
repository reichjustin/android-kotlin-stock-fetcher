package com.example.stockticker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.widget.ProgressBar
import com.example.stockticker.handlers.FetchQuoteHandler
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get all of the UI items needed for the handlers
        val fetchQuoteButton: Button = findViewById(R.id.get_quote)
        val priceView: TextView = findViewById(R.id.price)
        val stockText: TextView = findViewById(R.id.stock_ticker)
        val spinner: ProgressBar = findViewById(R.id.progressBar)
        setupHandlers(priceView, stockText, spinner, fetchQuoteButton)
    }

    private fun setupHandlers(priceView: TextView, stockText: TextView, spinner: ProgressBar, fetchQuoteButton: Button) {
        fetchQuoteButton.setOnClickListener {
            priceView.visibility = View.GONE

            val ticker = stockText.text
            if (ticker === null || ticker.length === 0) {
                priceView.visibility = View.VISIBLE
                priceView.text = "No ticker provided"
            } else {
                spinner.visibility = View.VISIBLE

                // launch a new supervisor job on the main (UI) thread
                val job = SupervisorJob()
                GlobalScope.launch(job + Dispatchers.Main) {
                    // simulate some network delay so the spinner gets it's shine
                    delay(300)

                    // await on the fetch to get the price as string
                    val price: String? = FetchQuoteHandler.fetchStockQuoteAsync(ticker.toString()).await()

                    if (price === null) {
                        priceView.text = "Unable to retrieve price"
                    } else {
                        priceView.text = "$$price"
                    }

                    spinner.visibility = View.GONE
                    priceView.visibility = View.VISIBLE
                }
            }
        }
    }
}
