package com.example.stockticker.handlers

import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

interface IFetchQuoteHandler {
    suspend fun fetchStockQuoteAsync(ticker: String): Deferred<String?>
}

object FetchQuoteHandler : IFetchQuoteHandler {
    private const val _url = "http://10.0.2.2:9876/price"
    private val _httpClient = OkHttpClient()

    /**
     * Launches a new IO coroutine to retrieve a given ticker's stock price
     *
     * @return Deferred<String?> - Await-able response of String or Null for
     *  retrieving a given ticker's stock price
     */
    override suspend fun fetchStockQuoteAsync(ticker: String): Deferred<String?> {
        return coroutineScope {
            async(Dispatchers.IO) {
                val request = Request
                    .Builder()
                    .url("$_url/$ticker")
                    .build()

                val result = _httpClient.newCall(request).execute().body()?.string()
                JSONObject(result).get("close")?.toString()
            }
        }
    }
}