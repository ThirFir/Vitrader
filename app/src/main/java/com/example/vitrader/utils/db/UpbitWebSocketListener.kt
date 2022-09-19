package com.example.vitrader.utils.db

import android.util.Log
import com.example.vitrader.utils.UpbitAPI
import com.example.vitrader.utils.model.Coin
import com.example.vitrader.utils.model.CoinRepository
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import java.io.IOException

object UpbitWebSocketListener : WebSocketListener() {
    private val gson = Gson()

    var infoList = mutableListOf<Coin.Info>()
    var sendParameter = ""


    fun launchGettingExternalCoinData() {
        val client = OkHttpClient()
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

                sendParameter = "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":[${infoString.substring(0, infoString.length-1)}]}]"
                client.newWebSocket(tickerRequest, this@UpbitWebSocketListener)
                client.dispatcher.executorService.shutdown()

            }
        })

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(sendParameter)
        //webSocket.close(NORMAL_CLOSURE_STATUS, null) //없을 경우 끊임없이 서버와 통신함
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("Socket1","Receiving : $text")
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        //Log.d("Socket2", "통신")

        val ticker = gson.fromJson(bytes.utf8(), Coin.Ticker::class.java)

        if(CoinRepository.coins.size < infoList.size){
            CoinRepository.addCoin(Coin(infoList[CoinRepository.coins.size], ticker))
        } else {
            CoinRepository.updateTicker(ticker)
        }

    }


    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("Socket3","Closing : $code / $reason")
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("Socket4","Error : " + t.message)
        //launchGettingExternalCoinData()
    }

    private const val NORMAL_CLOSURE_STATUS = 1000

}

