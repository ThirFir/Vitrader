package com.example.vitrader.utils.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.vitrader.utils.SymbolFormat
import com.example.vitrader.utils.UpbitAPI
import com.example.vitrader.utils.db.UpbitWebSocketListener2.NORMAL_CLOSURE_STATUS
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.model.CoinRepository
import com.example.vitrader.utils.model.OrderBook
import com.example.vitrader.utils.model.OrderBookRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

object UpbitWebSocketListener : WebSocketListener() {
    private val gson = Gson()

    private var infoList = mutableListOf<Coin.Info>()
    var isCoinLoadCompleted = mutableStateOf(false)
    private var tickerSendParameter = ""

    fun launchGettingExternalCoinData() {
        Log.d("upbit", "launch")
        val client = OkHttpClient().newBuilder().retryOnConnectionFailure(true).build()
        val infoRequest = Request.Builder().url(UpbitAPI.BASE_URL + UpbitAPI.ALL_COIN_SUB_URL).build()

        val gson = Gson()

        infoList.clear()
        client.newCall(infoRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("UpbitWebSocketListener", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                val body_str_of_all_infos = response.body!!.string()
                val infos = gson.fromJson(body_str_of_all_infos, Array<Coin.Info>::class.java)

                val tickerRequest = Request.Builder().url(UpbitAPI.WEB_SOCKET).build()

                var infoString = ""
                for(info in infos) {
                    if(info.symbol.startsWith("KRW")) {
                        infoString += "\"" + info.symbol + "\"" + ","
                        infoList.add(info)
                    }
                }
                tickerSendParameter = "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[${infoString.substring(0, infoString.length-1)}]}]"

                client.newWebSocket(tickerRequest, this@UpbitWebSocketListener)
                client.dispatcher.executorService.shutdown()
            }
        })

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(tickerSendParameter)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Socket1","Receiving : $text")
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        val ticker = gson.fromJson(bytes.utf8(), Coin.Ticker::class.java)
        val formattedSymbol = SymbolFormat.get(ticker.market)
        Log.d("Socket2", formattedSymbol)

        if(CoinRepository.coins.size < infoList.size){
            isCoinLoadCompleted.value = false
            CoinRepository.addCoin(Coin(infoList[CoinRepository.coins.size], ticker, getCoinImage(formattedSymbol)))
        } else {
            isCoinLoadCompleted.value = true
            CoinRepository.updateTicker(ticker)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("coin list Socket3","Closing : $code / $reason")
        // launchGettingExternalCoinData()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("coin list Socket4","Error : " + t.message)
        webSocket.close(NORMAL_CLOSURE_STATUS, reason = t.message)
        isCoinLoadCompleted.value = false
        launchGettingExternalCoinData()
    }

    private fun getCoinImage(formattedSymbol: String): Bitmap {
        val imageUrl = URL("https://static.upbit.com/logos/$formattedSymbol.png")
        return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream())
    }

    private const val NORMAL_CLOSURE_STATUS = 1000

}

object UpbitWebSocketListener2 : WebSocketListener() {
    private val gson = Gson()

    private var infoList = mutableListOf<Coin.Info>()
    private var orderBookSendParameter = ""

    fun launchGettingExternalOrderBookData() {
        Log.d("upbit", "launch")
        val client = OkHttpClient().newBuilder().retryOnConnectionFailure(true).build()
        val infoRequest = Request.Builder().url(UpbitAPI.BASE_URL + UpbitAPI.ALL_COIN_SUB_URL).build()

        val gson = Gson()

        infoList.clear()
        client.newCall(infoRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("UpbitWebSocketListener", e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                val body_str_of_all_infos = response.body!!.string()
                val infos = gson.fromJson(body_str_of_all_infos, Array<Coin.Info>::class.java)

                val tickerRequest = Request.Builder().url(UpbitAPI.WEB_SOCKET).build()

                var infoString = ""
                for(info in infos) {
                    if(info.symbol.startsWith("KRW")) {
                        infoString += "\"" + info.symbol + "\"" + ","
                        infoList.add(info)
                    }
                }
                orderBookSendParameter = "[{\"ticket\":\"test\"},{\"type\":\"orderbook\",\"codes\":[${infoString.substring(0, infoString.length-1)}]}]"

                client.newWebSocket(tickerRequest, this@UpbitWebSocketListener2)
                client.dispatcher.executorService.shutdown()
            }
        })

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(orderBookSendParameter)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Socket1","Receiving : $text")
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        val orderBook = gson.fromJson(bytes.utf8(), OrderBook::class.java)
        OrderBookRepository.updateOrderBook(orderBook)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Socket3","Closing : $code / $reason")
        launchGettingExternalOrderBookData()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("Socket4","Error : " + t.message)
        webSocket.close(NORMAL_CLOSURE_STATUS, reason = t.message)
    }

    private const val NORMAL_CLOSURE_STATUS = 1000

}